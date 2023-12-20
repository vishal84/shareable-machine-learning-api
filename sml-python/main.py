import os
import uuid
import json
import requests
import google.auth
import google.auth.transport.requests
from google.cloud import aiplatform_v1beta1
from google.cloud import storage
from flask import Flask, request

app = Flask(__name__)


@app.route("/")
def hello_world():
    return "Hello from Python SML"


@app.route('/forecasting', methods=['POST'])
def forecasting():
    data = request.get_json()
    project = "qwiklabs-resources"
    location = "us-central1"
    uuid_value = uuid.uuid4().hex
    display_name = "qwiklabs-student" + uuid_value
    model_name = "projects/" + project + "/locations/" + \
        location + "/models/" + data["model_id"]
    gcs_source_uri = data["gcs_source_uri"]
    gcs_destination_output_uri_prefix = data["gcs_destination_output_uri_prefix"]
    predictions_format = data["predictions_format"]
    api_endpoint = "us-central1-aiplatform.googleapis.com"

    # The AI Platform services require regional API endpoints.
    client_options = {"api_endpoint": api_endpoint}
    # Initialize client that will be used to create and send requests.
    # This client only needs to be created once, and can be reused for multiple requests.
    client = aiplatform_v1beta1.JobServiceClient(client_options=client_options)

    batch_prediction_job = {
        "display_name": display_name,
        # Format: 'projects/{project}/locations/{location}/models/{model_id}'
        "model": model_name,
        "input_config": {
            "instances_format": predictions_format,
            "gcs_source": {"uris": [gcs_source_uri]},
        },
        "output_config": {
            "predictions_format": predictions_format,
            "gcs_destination": {"output_uri_prefix": gcs_destination_output_uri_prefix},
        },
    }

    parent = f"projects/{project}/locations/{location}"
    response = client.create_batch_prediction_job(
        parent=parent, batch_prediction_job=batch_prediction_job
    )

    return {
        "message": "Prediction job will take approximately to complete.",
        "job_id": response.name.split('/')[-1],
        "state": str(response.state)
    }


@app.route('/forecasting/status', methods=['POST'])
def forecasting_status():
    data = request.get_json()
    print(data["job_id"])
    project = "qwiklabs-resources"
    location = "us-central1"
    api_endpoint = "us-central1-aiplatform.googleapis.com"
    job_id = "projects/" + project + "/locations/" + \
        location + "/batchPredictionJobs/" + data["job_id"]
    client_options = {"api_endpoint": api_endpoint}
    client = aiplatform_v1beta1.JobServiceClient(client_options=client_options)
    response = client.get_batch_prediction_job(name=job_id)

    print("response:", response)
    print(str(response.state))
   
    return {
        "state": str(response.state),
        "gcs_output_directory": response.output_info.gcs_output_directory
    }


def get_token():
    credentials, your_project_id = google.auth.default(scopes=["https://www.googleapis.com/auth/cloud-platform"])
    auth_req = google.auth.transport.requests.Request()
    credentials.refresh(auth_req) #refresh token
    return credentials.token

def split_gcs_path(gcs_path):
    path_parts=gcs_path.replace("gs://","").split("/")
    bucket=path_parts.pop(0)
    key="/".join(path_parts)
    return bucket, key

@app.route('/evaluations', methods=['POST'])
def pipelines_forecasting():
    data = request.get_json()
    model_id = data["model_id"]
    gcs_destination_output = data["gcs_destination_output_uri_prefix"]
    req_url = f"https://us-central1-aiplatform.googleapis.com/v1/projects/qwiklabs-resources/locations/us-central1/models/{model_id}/evaluations"
    
    id_token = get_token()
    headers = {
      'Authorization': 'Bearer {}'.format(id_token),
      'Content-Type': 'application/json'
    }
    response = requests.request("GET", req_url, headers=headers)
    
    # Write response to student's bucket
    bucket_name, key = split_gcs_path(gcs_destination_output)
    bucket = storage.Client().get_bucket(bucket_name)
    blob = bucket.blob('evaluations.json')
    blob.upload_from_string(data=json.dumps(response.json()),content_type='application/json')

    return {
        "gcs_destination_output": f"https://storage.cloud.google.com/{bucket_name}/evaluations.json",
        "evaluations": response.json()
    }

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=int(os.environ.get("PORT", 8080)))
