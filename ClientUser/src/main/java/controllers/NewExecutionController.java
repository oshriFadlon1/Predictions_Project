package controllers;

import com.google.gson.Gson;
import dtos.DtoEnvironmentDetails;
import dtos.DtoResponsePreview;
import dtos.DtoUiToEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import okhttp3.*;
import org.example.Main;
import org.jetbrains.annotations.NotNull;
import presenters.EntityPresenter;
import presenters.EnvironmentPresenter;
import range.Range;
import userutilities.UserUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class NewExecutionController implements Initializable {
    private int population1 = 0;
    private int population2 = 0;
    private UserMenuController userMenuController;
    private Gson gson;
    private OkHttpClient client;
    private Map<String, Object> envToValue;
    private DtoResponsePreview worldPreview;
    private EnvironmentPresenter currentSelectedItem;
    private ObservableList<EntityPresenter> obsListEntities;
    private ObservableList<EnvironmentPresenter> obsListEnvironments;
    private ObservableList<EnvironmentPresenter> obsListEnvironmentsBefore;
    private Alert alertWindow;
    private int currentRequest;

    @FXML
    private Button buttonClear;

    @FXML
    private Button buttonStart;

    @FXML
    private Button buttonValue;

    @FXML
    private Button buttonValueEntity1;

    @FXML
    private Button buttonValueEntity2;

    @FXML
    private Label entity1Label;

    @FXML
    private Label entity2Label;

    @FXML
    private TableColumn<EntityPresenter, String> entityColumn;

    @FXML
    private TableColumn<EnvironmentPresenter, String> envNameCol;

    @FXML
    private TableColumn<EnvironmentPresenter, Range> envRangeCol;

    @FXML
    private TableColumn<EnvironmentPresenter, String> envTypeCol;

    @FXML
    private TableColumn<EnvironmentPresenter, String> environmentColumn;

    @FXML
    private Label labelError;

    @FXML
    private Label labelErrorEntity1;

    @FXML
    private Label labelErrorEntity2;

    @FXML
    private Label labelValue;

    @FXML
    private TableColumn<EntityPresenter, Integer> populationColumn;

    @FXML
    private Label subTitleEntity2;

    @FXML
    private TableView<EntityPresenter> tableEntities;

    @FXML
    private TableView<EnvironmentPresenter> tableEnvironments;

    @FXML
    private TableView<EnvironmentPresenter> tableEnvironmentsPresenter;

    @FXML
    private TextField textFieldEntity1;

    @FXML
    private TextField textFieldEntity2;

    @FXML
    private TextField textFieldValue;

    @FXML
    private TableColumn<EnvironmentPresenter, Object> valueColumn;

    @FXML
    private VBox vboxValues;

    public int getCurrentRequest() {
        return currentRequest;
    }

    public void setCurrentRequest(int currentRequest) {
        this.currentRequest = currentRequest;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.obsListEnvironmentsBefore = FXCollections.observableArrayList();
        this.obsListEnvironments = FXCollections.observableArrayList();
        this.obsListEntities = FXCollections.observableArrayList();
        this.envNameCol.setCellValueFactory(new PropertyValueFactory<>("environmentName"));
        this.envRangeCol.setCellValueFactory(new PropertyValueFactory<>("environmentRange"));
        this.envTypeCol.setCellValueFactory(new PropertyValueFactory<>("environmentType"));
        this.environmentColumn.setCellValueFactory(new PropertyValueFactory<>("environmentName"));
        this.valueColumn.setCellValueFactory(new PropertyValueFactory<>("environmentVal"));
        this.entityColumn.setCellValueFactory(new PropertyValueFactory<>("entityName"));
        this.populationColumn.setCellValueFactory(new PropertyValueFactory<>("population"));
        this.tableEntities.setItems(this.obsListEntities);
        this.tableEnvironments.setItems(this.obsListEnvironments);
        this.tableEnvironmentsPresenter.setItems(this.obsListEnvironmentsBefore);
        this.gson = new Gson();
        this.client = Main.getClient();
    }

    @FXML
    void OnClickSetValueOfPopulationEntity1(ActionEvent event) {
        String valueOfTextField = textFieldEntity1.getText();
        int sizeOfWorld = this.worldPreview.getRow() * this.worldPreview.getCol();
        if (!UserUtilities.isInteger(valueOfTextField)) {
            labelErrorEntity1.setText("Invalid value for population.\nPlease enter a number between 0 to " + (sizeOfWorld - this.population2));
            labelErrorEntity1.setVisible(true);
            return;
        }
        else {
            int parsedValue = Integer.parseInt(valueOfTextField);
            if (parsedValue > sizeOfWorld - this.population2) {
                labelErrorEntity1.setText("Quantity of population cannot be bigger than actual world size.\nPlease enter a number between 0 to " + (sizeOfWorld - this.population2));
                labelErrorEntity1.setVisible(true);
            }
            else{
                labelErrorEntity1.setVisible(true);
                this.population1 = parsedValue;
                this.labelErrorEntity1.setText(this.entity1Label.getText() + " population: " + this.population1);
                EntityPresenter entityPresenter = new EntityPresenter(this.entity1Label.getText(), this.population1);
                addEntityToObserverList(entityPresenter);
                this.population1 = entityPresenter.getStartPopulation();
                this.tableEntities.setVisible(true);
            }
        }

    }
    private void addEntityToObserverList(EntityPresenter presenterToAdd){
        EntityPresenter isEntityExists = isEntityNameExistsInList(presenterToAdd);
        if(isEntityExists == null) {
            this.obsListEntities.add(presenterToAdd);
        }
        else{
            this.obsListEntities.set(this.obsListEntities.indexOf(isEntityExists), presenterToAdd);
        }
    }
    private EntityPresenter isEntityNameExistsInList(EntityPresenter entityPresenter){
        for(EntityPresenter currPresenter: this.obsListEntities){
            if(entityPresenter.getEntityName().equalsIgnoreCase(currPresenter.getEntityName())){
                return currPresenter;
            }
        }
        return null;
    }

    @FXML
    void OnClickSetValueOfPopulationEntity2(ActionEvent event) {
        String valueOfTextField = textFieldEntity2.getText();
        int sizeOfWorld = this.worldPreview.getRow() * this.worldPreview.getCol();
        if(!UserUtilities.isInteger(valueOfTextField)){
            labelErrorEntity2.setText("Invalid value for population.\nPlease enter a number between 0 to " + (sizeOfWorld - this.population1));
            labelErrorEntity2.setVisible(true);
        }
        else {
            int parsedValue = Integer.parseInt(valueOfTextField);
            if(parsedValue > sizeOfWorld - this.population1){
                labelErrorEntity2.setText("Quantity of population cannot be bigger than actual world size.\nPlease enter a number between 0 to " + (sizeOfWorld - this.population1));
                labelErrorEntity2.setVisible(true);
            }
            else{
                labelErrorEntity2.setVisible(true);
                this.population2 = parsedValue;
                this.labelErrorEntity2.setText(this.entity2Label.getText() + " population: " + this.population2);
                EntityPresenter entityPresenter = new EntityPresenter(this.entity2Label.getText(), this.population2);
                addEntityToObserverList(entityPresenter);
                this.population2 = entityPresenter.getStartPopulation();
                this.tableEntities.setVisible(true);
            }
        }
    }

    @FXML
    void OnClickedClear(ActionEvent event) {
        this.envToValue.clear();
        this.labelErrorEntity1.setVisible(false);
        this.labelErrorEntity2.setVisible(false);
        this.textFieldEntity2.setText("");
        this.textFieldValue.setText("");
        this.textFieldEntity1.setText("");
        this.population1 = 0;
        this.population2 = 0;
        labelError.setText("All chosen environment and population values were removed");
        this.obsListEntities.clear();
        //this.obsListEnvironments.clear();
        this.tableEntities.setVisible(false);
        //this.tableEnvironments.setVisible(false);
    }

    @FXML
    void OnClickedSetValueOfEnvProp(ActionEvent event) {
        checkValidationEnvironment();
    }

    public void checkValidationEnvironment(){
        if(this.currentSelectedItem == null){
            this.labelError.setVisible(true);
            this.labelError.setText("You haven't selected a value yet");
            return;
        }
        String valueToCheck = textFieldValue.getText();
        String envName = this.currentSelectedItem.getEnvironmentName();
        Map<String, DtoEnvironmentDetails> envDefs = this.worldPreview.getDtoEnvironments();
        DtoEnvironmentDetails envDef = envDefs.get(envName);
        String envType = envDef.getPropertyType();
        if(!isTypeValid(envType, valueToCheck)){
            labelError.setVisible(true);
            labelError.setText("Environment value is not matching to it's type");
            return;
        }
        if(envType.equalsIgnoreCase("float")){
            if(!isRangeValid(envDef.getFrom(), envDef.getTo(), valueToCheck)){
                labelError.setVisible(true);
                labelError.setText("Environment value is not in range");
                return;
            }
            this.envToValue.put(envName,Float.parseFloat(valueToCheck));
        }
        else if (envType.equalsIgnoreCase("boolean")){
            this.envToValue.put(envName,Boolean.parseBoolean(valueToCheck));
        }
        else {
            this.envToValue.put(envName,valueToCheck);
        }
        SaveEnvPropValueSuccessfully(valueToCheck, envName);
    }

    private void SaveEnvPropValueSuccessfully(String valueToCheck, String envName) {
        labelError.setVisible(true);
        labelError.setText("Value " + valueToCheck + " was set to environment " + envName + " succesfully");
        this.labelValue.setText("");
        textFieldValue.setText("");
        EnvironmentPresenter envPresenter = new EnvironmentPresenter(envName, valueToCheck);
        EnvironmentPresenter isEnvExist = isEnvNameExistsInList(envPresenter);
        if(isEnvNameExistsInList(envPresenter) == null) {
            this.obsListEnvironments.add(envPresenter);
        }
        else{
            this.obsListEnvironments.set(this.obsListEnvironments.indexOf(isEnvExist), envPresenter);
        }
        this.tableEnvironments.setVisible(true);
    }

    private EnvironmentPresenter isEnvNameExistsInList(EnvironmentPresenter environmentPresenter){
        for(EnvironmentPresenter currPresenter: this.obsListEnvironments){
            if(environmentPresenter.getEnvironmentName().equalsIgnoreCase(currPresenter.getEnvironmentName())){
                return currPresenter;
            }
        }
        return null;
    }


    private boolean isTypeValid(String propertyType,String value) {
        if(propertyType.equalsIgnoreCase("boolean")){
            if(!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")){
                return false;
            }
        }
        else if(propertyType.equalsIgnoreCase("float")){
            if(!UserUtilities.isFloat(value)){
                return false;
            }
        }

        return true;
    }

    private boolean isRangeValid(float from, float to, String valueToCheck) {
        float parsedVal = Float.parseFloat(valueToCheck);
        return parsedVal >= from && parsedVal <= to;
    }

    @FXML
    void OnClickedStartSimulation(ActionEvent event) {
        //TODO change implementation. first present the table and then start simulation
        String simulationName = this.worldPreview.getSimulationName();
        int population1 = Integer.parseInt(this.textFieldEntity1.getText());
        int population2 = Integer.parseInt(this.textFieldEntity2.getText());
        String primaryEntityName = this.entity1Label.getText();
        String secondaryEntityName = this.entity2Label.getText();
        DtoUiToEngine dtoSimulationDetails = new DtoUiToEngine(simulationName, this.envToValue, population1, population2, primaryEntityName, secondaryEntityName);
        // TODO Add popup with all the information
        String jsonPayload = this.gson.toJson(dtoSimulationDetails);
        RequestBody requestBody = RequestBody.create(jsonPayload, null);
        Request startSimulationRequest = new Request.Builder().url(Main.getBaseUrl() + "/user/executeSimulation?requestId=" + this.currentRequest).put(requestBody).build();
        Call call = this.client.newCall(startSimulationRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                alertWindow = new Alert(Alert.AlertType.INFORMATION);
                StringBuilder finalDetailsToShow = new StringBuilder(String.format("Entities\n{0}: {1}\n{2}: {3}", entity1Label.getText(), textFieldEntity1.getText(),
                        !entity2Label.getText().equalsIgnoreCase("") ? entity2Label.getText() : "", !textFieldEntity2.getText().equalsIgnoreCase("")
                                ? textFieldEntity2.getText() : ""));
                finalDetailsToShow.append("Environments: \n");
                for(String envName: envToValue.keySet()){
                    finalDetailsToShow.append(envName);
                    finalDetailsToShow.append(": ").append(envToValue.get(envName));
                    finalDetailsToShow.append("\n");
                }
                alertWindow.setContentText(finalDetailsToShow.toString());
                alertWindow.show();
                userMenuController.switchToResultsTab();
            }
        });
    }

    @FXML
    void selectItem(MouseEvent event) {
        EnvironmentPresenter item = this.tableEnvironmentsPresenter.getSelectionModel().getSelectedItem();
        if(item != null){
            this.currentSelectedItem = item;
        }
    }

    public void setWorldPreview(DtoResponsePreview chosenWorld) {
        this.worldPreview = chosenWorld;
    }

    public void initializeRandomEnvironmentValues() {
        Object initVal = null;
        for(String envName: this.worldPreview.getDtoEnvironments().keySet()){
            switch (this.worldPreview.getDtoEnvironments().get(envName).getPropertyType().toLowerCase()){
                case "float":
                    float initEnvValFloat = UserUtilities.initializeRandomFloat(this.worldPreview.getDtoEnvironments().get(envName).getFrom(),
                            this.worldPreview.getDtoEnvironments().get(envName).getTo());
                    initVal = initEnvValFloat;
                    break;
                case "string":
                    String initEnvValString = UserUtilities.initializeRandomString();
                    initVal = initEnvValString;
                    break;
                case "boolean":
                    boolean initEnvValBoolean = UserUtilities.initializeRandomBoolean();
                    initVal = initEnvValBoolean;
                    break;
                default:
                    break;
            }
            this.envToValue.put(envName, initVal);
        }
    }

    public void setUserMenuController(UserMenuController userMenuController) {
        this.userMenuController = userMenuController;
    }
}
