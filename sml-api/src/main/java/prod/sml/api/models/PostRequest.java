package prod.sml.api.models;

public class PostRequest {
    String modelId;
    String inputUri;
    String outputUri;
    String modelRegion;
    String fileName;

    public String getModelId() {
        return modelId;
    }

    public String getInputUri() {
        return inputUri;
    }

    public String getOutputUri() {
        return outputUri;
    }

    public String getModelRegion() {
        return modelRegion;
    }

    public String getFileName() {
        return fileName;
    }

    public void setId(String modelId) {
        this.modelId = modelId;
    }
}