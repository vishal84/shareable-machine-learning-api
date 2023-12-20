package prod.sml.api.controller;

import com.google.cloud.automl.v1beta1.*;
import com.google.longrunning.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import prod.sml.api.models.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class WebController {

    @RequestMapping("/")
    public String index() {
        return "Hello from the SML API!";
    }

    @RequestMapping(value = "/vertex/predict/tabular_classification", method = RequestMethod.POST)
    TabularClassificationPredictionPostResponse TabularClassificationPrediction (@RequestBody TabularClassificationPredictionPostRequest inputPayload) throws IOException {
        System.out.println("*** start /vertex/predict/tabular_classification");
        TabularClassificationPredictionPostResponse response = new TabularClassificationPredictionPostResponse();
        String project = "qwiklabs-resources";
        String endpointId = inputPayload.getEndpointId();
        String instance = inputPayload.getInstance();
//        String instance = "[{Amount:19.56,Time:79770,V1:-0.24,V2:0.064,V3:0.064,V4:-0.16,V5:-0.152,V6:-0.3,V7:-0.13,V8:-0.01,V9:-0.01,V10:-0.179676312288214,V11:-0.149748578100389,V12:0.058843299296498,V13:-0.12500565204219502,V14:-0.0122376472088033,V15:-0.0804581914854576,V16:-0.0142596460749478,V17:-0.0142596460749478,V18:-0.0142596460749478,V19:-0.0142596460749478,V20:-0.0142596460749478,V21:-0.0142596460749478,V22:-0.0142596460749478,V23:-0.0142596460749478,V24:-0.0142596460749478,V25:-0.0142596460749478,V26:-0.0142596460749478,V27:-0.0142596460749478,V28:-0.0142596460749478 }]";



        List<? extends Serializable> responseList = TabularClassificationPrediction.predictTabularClassification(project, endpointId, instance);
        System.out.println(responseList.get(0));
        response.setModelClass((String) responseList.get(0));
        response.setModelScore((Float) responseList.get(1));
        return response;
    }

    @RequestMapping(value = "/vertex/image_classification", method = RequestMethod.POST)
    PostResponse VertexImageClassification (@RequestBody VertexImageClassificationPostRequest inputPayload) throws IOException {
        PostResponse response = new PostResponse();
        String project = "qwiklabs-resources";
        String fileName = inputPayload.getFileName();
        String endpointId = "1080028281731809280"; // convert to inputPayload

        PredictImageClassification.predictImageClassification(project,fileName,endpointId);
        response.setOperationStatus(true);
        return response;

    }

    @RequestMapping(value = "/automl_tables/csv", method = RequestMethod.POST)
    PostResponse AutoMlTablesCsv (@RequestBody PostRequest inputPayload) throws IOException, ExecutionException, InterruptedException {
        PostResponse response = new PostResponse();
        System.out.println();
        try (PredictionServiceClient client = PredictionServiceClient.create()) {
            // Get the full path of the model.
            ModelName name = ModelName.of("qwiklabs-resources", inputPayload.getModelRegion(), inputPayload.getModelId());

            // Configure the source of the file from a GCS bucket
            GcsSource gcsSource = GcsSource.newBuilder().addInputUris(inputPayload.getInputUri()).build();
            BatchPredictInputConfig inputConfig =
                    BatchPredictInputConfig.newBuilder().setGcsSource(gcsSource).build();

            // Configure where to store the output in a GCS bucket
            GcsDestination gcsDestination =
                    GcsDestination.newBuilder().setOutputUriPrefix(inputPayload.getOutputUri()).build();
            BatchPredictOutputConfig outputConfig =
                    BatchPredictOutputConfig.newBuilder().setGcsDestination(gcsDestination).build();

            // Build the request that will be sent to the API
            BatchPredictRequest request =
                    BatchPredictRequest.newBuilder()
                            .setName(name.toString())
                            .setInputConfig(inputConfig)
                            .setOutputConfig(outputConfig)
                            .build();

            // Start an asynchronous request
            String operationName = client.batchPredictAsync(request).getName();
            response.setOperation(operationName);

        }
        return response;
    }

    // AutoML Tables - Using CSV files in Cloud Storage
    @RequestMapping(value = "/automl_tables/csv/status", method = RequestMethod.POST)
    TableStatusPostResponse AutoMlTablesCsvStatus (@RequestBody TablesStatusPostRequest inputPayload) throws IOException, ExecutionException, InterruptedException {
        TableStatusPostResponse response = new TableStatusPostResponse();
        try (PredictionServiceClient client = PredictionServiceClient.create()) {

            Operation operation = client.getOperationsClient().getOperation(inputPayload.getOperationFullId());
            response.setOperationStatus(operation.getDone());

        }
        return response;
    }
}

