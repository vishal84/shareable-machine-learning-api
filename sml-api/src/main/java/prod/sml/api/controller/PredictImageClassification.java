package prod.sml.api.controller;

import com.google.api.client.util.Base64;
import com.google.cloud.aiplatform.util.ValueConverter;
import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.cloud.aiplatform.v1.schema.predict.instance.ImageClassificationPredictionInstance;
import com.google.cloud.aiplatform.v1.schema.predict.params.ImageClassificationPredictionParams;
import com.google.cloud.aiplatform.v1.schema.predict.prediction.ClassificationPredictionResult;
import com.google.protobuf.Value;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PredictImageClassification {

    public static void main(String[] args) throws IOException {
        // TODO(developer): Replace these variables before running the sample.
        String project = args[0];
        String fileName = args[1];
        String endpointId = args[2];
        predictImageClassification(project, fileName, endpointId);
    }

    static void predictImageClassification(String project, String fileName, String endpointId)
            throws IOException {
        PredictionServiceSettings settings =
                PredictionServiceSettings.newBuilder()
                        .setEndpoint("us-central1-aiplatform.googleapis.com:443")
                        .build();
        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try (PredictionServiceClient predictionServiceClient =
                     PredictionServiceClient.create(settings)) {
            String location = "us-central1";
            EndpointName endpointName = EndpointName.of(project, location, endpointId);

            byte[] contents = Base64.encodeBase64(Files.readAllBytes(Paths.get(fileName)));
            String content = new String(contents, StandardCharsets.UTF_8);

            ImageClassificationPredictionInstance predictionInstance =
                    ImageClassificationPredictionInstance.newBuilder().setContent(content).build();

            List<Value> instances = new ArrayList<>();
            instances.add(ValueConverter.toValue(predictionInstance));

            ImageClassificationPredictionParams predictionParams =
                    ImageClassificationPredictionParams.newBuilder()
                            .setConfidenceThreshold((float) 0.5)
                            .setMaxPredictions(5)
                            .build();

            PredictResponse predictResponse =
                    predictionServiceClient.predict(
                            endpointName, instances, ValueConverter.toValue(predictionParams));
            System.out.println("Predict Image Classification Response");
            System.out.format("\tDeployed Model Id: %s\n", predictResponse.getDeployedModelId());

            System.out.println("Predictions");
            for (Value prediction : predictResponse.getPredictionsList()) {

                ClassificationPredictionResult.Builder resultBuilder =
                        ClassificationPredictionResult.newBuilder();
                // Display names and confidences values correspond to
                // IDs in the ID list.
                ClassificationPredictionResult result =
                        (ClassificationPredictionResult) ValueConverter.fromValue(resultBuilder, prediction);
                int counter = 0;

                for (Long id : result.getIdsList()) {
                    System.out.printf("Label ID: %d\n", id);
                    System.out.printf("Label: %s\n", result.getDisplayNames(counter));
                    System.out.printf("Confidence: %.4f\n", result.getConfidences(counter));
                    counter++;
                }
            }
        }
    }
}
// [END aiplatform_predict_image_classification_sample]
