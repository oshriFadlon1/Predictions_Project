package adminComponent.controller;

import dtos.DtoPropertyDetail;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EntityComponentController {
    @FXML
    private Label entPropName;

    @FXML
    private Label propNameValue;

    @FXML
    private Label propRange;

    @FXML
    private Label propType;

    @FXML
    private Label randomInit;

    @FXML
    private Label randomValue;

    @FXML
    private Label rangeValue;

    @FXML
    private Label typeValue;

    public void updateLabelEnt(DtoPropertyDetail propertyDefinitionChoose){
        if (propertyDefinitionChoose == null){
            return;
        }

        entPropName.setText("Entity property name: ");
        propNameValue.setText(propertyDefinitionChoose.getPropertyName());
        propType.setText("Entity property type: ");
        typeValue.setText(propertyDefinitionChoose.getPropertyType());
        if (propertyDefinitionChoose.getFrom() != -1) {
            rangeValue.setText(propertyDefinitionChoose.getFrom() + " -> " + propertyDefinitionChoose.getTo());
        }else {
            rangeValue.setText("The following property variable has no range of values.");

        }
        boolean randomInitialize = propertyDefinitionChoose.getRandomInit();
        String initTo = propertyDefinitionChoose.getInit();
        if (randomInitialize){
            randomValue.setText("Random-initialize");
        }else{
            randomValue.setText("initialize to " + initTo);
        }
    }
}
