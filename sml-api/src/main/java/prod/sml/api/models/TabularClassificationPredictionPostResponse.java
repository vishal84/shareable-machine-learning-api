package prod.sml.api.models;

import org.springframework.web.servlet.ModelAndView;

public class TabularClassificationPredictionPostResponse {
    String model_class;
    Float model_score;
    public void setModelClass(String model_class) {
        this.model_class = model_class;
    }
    public void setModelScore(Float model_score) {
        this.model_score = model_score;
    }
}