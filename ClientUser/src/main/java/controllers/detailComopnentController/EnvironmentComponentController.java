package controllers.detailComopnentController;

import dtos.DtoEnvironmentDetails;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EnvironmentComponentController {
    @FXML
    private Label envName;

    @FXML
    private Label envRange;

    @FXML
    private Label envType;

    @FXML
    private Label valueOfName;

    @FXML
    private Label valueOfRange;

    @FXML
    private Label valueOfType;

    public void updateLabelEnv(DtoEnvironmentDetails environmentDefinition){
        if (environmentDefinition == null){
            return;
        }
        valueOfName.setText(environmentDefinition.getPropertyName());
        valueOfType.setText(environmentDefinition.getPropertyType());
        if (environmentDefinition.getFrom() == -1) {
            valueOfRange.setText("The following environment variable has no range of values.");
        }
        else {
            valueOfRange.setText(environmentDefinition.getFrom() + " -> " + environmentDefinition.getTo());
        }

    }
}
