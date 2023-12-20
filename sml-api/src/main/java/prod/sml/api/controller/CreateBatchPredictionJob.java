package prod.sml.api.controller;

import com.google.cloud.aiplatform.util.ValueConverter;
import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.cloud.aiplatform.v1.schema.predict.prediction.TabularClassificationPredictionResult;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class CreateBatchPredictionJob {
    public static void main(String[] args) throws IOException {
        String instance = "[{Amount:19.56,Time:79770,V1:-0.24,V2:0.064,V3:0.064,V4:-0.16,V5:-0.152,V6:-0.3,V7:-0.13,V8:-0.01,V9:-0.01,V10:-0.179676312288214,V11:-0.149748578100389,V12:0.058843299296498,V13:-0.12500565204219502,V14:-0.0122376472088033,V15:-0.0804581914854576,V16:-0.0142596460749478,V17:-0.0142596460749478,V18:-0.0142596460749478,V19:-0.0142596460749478,V20:-0.0142596460749478,V21:-0.0142596460749478,V22:-0.0142596460749478,V23:-0.0142596460749478,V24:-0.0142596460749478,V25:-0.0142596460749478,V26:-0.0142596460749478,V27:-0.0142596460749478,V28:-0.0142596460749478 }]";
        String project = "qwiklabs-resources";
        String endpointId = "2416330733665648640";
        createBatchPrediction(project, endpointId, instance);
    }

    static List<? extends Serializable> createBatchPrediction(String project, String endpointId, String instance)
            throws IOException {

        PredictionServiceSettings predictionServiceSettings =
                PredictionServiceSettings.newBuilder()
                        .setEndpoint("us-central1-aiplatform.googleapis.com:443")
                        .build();

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (PredictionServiceClient predictionServiceClient =
                     PredictionServiceClient.create(predictionServiceSettings)) {
            String location = "us-central1";
            EndpointName endpointName = EndpointName.of(project, location, endpointId);
            ListValue.Builder listValue = ListValue.newBuilder();
            JsonFormat.parser().merge(instance, listValue);
            List<Value> instanceList = listValue.getValuesList();
            Value parameters = Value.newBuilder().setListValue(listValue).build();
            PredictResponse predictResponse =
                    predictionServiceClient.predict(endpointName, instanceList, parameters);
            System.out.format("\tDeployed Model Id: %s\n", predictResponse.getDeployedModelId());

            System.out.println("Predictions");
            for (Value prediction : predictResponse.getPredictionsList()) {
                TabularClassificationPredictionResult.Builder resultBuilder =
                        TabularClassificationPredictionResult.newBuilder();
                TabularClassificationPredictionResult result =
                        (TabularClassificationPredictionResult)
                                ValueConverter.fromValue(resultBuilder, prediction);

                for (int i = 0; i < result.getClassesCount(); i++) {
                    System.out.println("Class: %s " + result.getClasses(i));
                    System.out.println("Score: %f " + result.getScores(i));
                }

                String model_class = result.getClasses(0);
                Float model_score = result.getScores(0);
                return Arrays.asList(model_class, model_score);
            }
        }
        return null;
    }
}