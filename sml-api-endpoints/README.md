# Adding new paths to Endpoints

https://cloud.google.com/endpoints/docs/openapi/get-started-cloud-run


Step 1:
gcloud endpoints services deploy openapi-run.yaml --project qwiklabs-resources


Step 2:
Copy Service Configuration from Step 1 output as:

```
Service Configuration [2021-07-29r3] uploaded for service [sml-api-kjyo252taq-uc.a.run.app]
```

Step 3:

Running the following, replace parameters:

```
chmod +x gcloud_build_image

./gcloud_build_image -s CLOUD_RUN_HOSTNAME -c CONFIG_ID -p qwiklabs-resources
```

Sample:
```
./gcloud_build_image -s sml-api-kjyo252taq-uc.a.run.app -c 2021-07-29r3 -p qwiklabs-resources
```


Step 4:
Copy the output from Step 3, example:

```
us.gcr.io/qwiklabs-resources/endpoints-runtime-serverless:2.29.1-sml-api-kjyo252taq-uc.a.run.app-2021-07-29r3

```

Step 5:

Running the following, replace parameters:

```
gcloud run deploy sml-api-endpoint \
  --image="us.gcr.io/qwiklabs-resources/endpoints-runtime-serverless:ESP_VERSION-CLOUD_RUN_HOSTNAME-CONFIG_ID" \
  --platform managed \
  --project=ESP_PROJECT_ID
```

Sample:

```
gcloud run deploy sml-api-endpoint --image="us.gcr.io/qwiklabs-resources/endpoints-runtime-serverless:2.29.1-sml-api-kjyo252taq-uc.a.run.app-2021-07-29r3" --platform managed --project=qwiklabs-resources
```

* Use the us-central1 region
* DO NOT ALLOW UNAUTHENTICATED

