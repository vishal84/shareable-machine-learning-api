package prod.sml.api.models;

import org.springframework.web.servlet.ModelAndView;

public class PostResponse {
    String operationId;
    String message;
    ModelAndView payload;
    boolean operationStatus;

    public void setOperation(String operationId) {
        this.operationId = operationId;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setPayload(ModelAndView payload) {
        this.payload = payload;
    }
    public void setOperationStatus(boolean operationStatus) {
        this.operationStatus = operationStatus;
    }

}