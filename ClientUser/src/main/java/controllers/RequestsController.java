package controllers;

import com.google.gson.Gson;
import dtos.admin.DtoAllSimulationRequests;
import dtos.admin.DtoSimulationsRequest;
import dtos.user.DtoRequestForAdmin;
import dtos.user.DtoTerminationForRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.*;
import org.example.Main;
import org.jetbrains.annotations.NotNull;
import presenters.UserRequestPresenter;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class RequestsController implements Initializable {
    private ObservableList<UserRequestPresenter> obsListRequests;
    private UserRequestPresenter currentSelectedRequestFromTable;
    private UserMenuController userMenuController;
    private Gson gson;
    private Thread fetchRequestsThread;
    @FXML
    private Label labelMsg;

    @FXML
    private Button buttonExecute;

    @FXML
    private Button buttonSubmitRequest;

    @FXML
    private CheckBox checkBoxFreeChoice;

    @FXML
    private CheckBox checkBoxSeconds;

    @FXML
    private CheckBox checkBoxTicks;

    @FXML
    private TableColumn<UserRequestPresenter, Integer> columnRequestId;

    @FXML
    private TableColumn<UserRequestPresenter, String> columnRequestStatus;

    @FXML
    private TableColumn<UserRequestPresenter, Integer> columnRequestedRuns;

    @FXML
    private TableColumn<UserRequestPresenter, String> columnSimulationName;

    @FXML
    private TableColumn<UserRequestPresenter, Integer> columnSimulationsRemain;

    @FXML
    private TableColumn<UserRequestPresenter, Integer> columnSimulationsRunning;

    @FXML
    private ComboBox<String> comboBoxSimulations;

    @FXML
    private TableView<UserRequestPresenter> tableViewRequests;

    @FXML
    private TextField textFieldNumberOfRuns;

    @FXML
    private TextField textFieldSeconds;

    @FXML
    private TextField textFieldTicks;
    private OkHttpClient client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.obsListRequests = FXCollections.observableArrayList();
        this.columnRequestId.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        this.columnRequestedRuns.setCellValueFactory(new PropertyValueFactory<>("requestedRuns"));
        this.columnRequestStatus.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));
        this.columnSimulationName.setCellValueFactory(new PropertyValueFactory<>("simulationName"));
        this.columnSimulationsRemain.setCellValueFactory(new PropertyValueFactory<>("simulationsRemaining"));
        this.columnSimulationsRunning.setCellValueFactory(new PropertyValueFactory<>("currentRuns"));
        this.tableViewRequests.setItems(obsListRequests);
        this.client = Main.getClient();
        this.gson = new Gson();
        this.fetchRequestsThread = new Thread(this:: fetchRequests);
        this.fetchRequestsThread.start();
    }

    private void fetchRequests() {
        while(true){
            try {
                Request userRequestsRequest = new Request.Builder().url(Main.getBaseUrl() + "/user/fetchRequests?userName=" + Main.getUserName()).build();
                Call call = this.client.newCall(userRequestsRequest);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        DtoAllSimulationRequests allSimulationsOfUser = gson.fromJson(response.body().string(), DtoAllSimulationRequests.class);
                        Map<Integer, DtoSimulationsRequest> mapOfAllSimulations = allSimulationsOfUser.getDtoSimulationsRequests();
                        for(Integer requestId: mapOfAllSimulations.keySet()) {
                            int requestIndxInListIfExists = checkIfRequestExist(requestId);
                            DtoSimulationsRequest currRequest = mapOfAllSimulations.get(requestId);
                            UserRequestPresenter requestInTable = new UserRequestPresenter(currRequest.getSimulationName(), currRequest.getRequestedRuns(), currRequest.getRequestStatus(),
                            currRequest.getCurrentRuns(), (currRequest.getFinishedRuns() - currRequest.getCurrentRuns()));
                            Platform.runLater(()->{
                                if(requestIndxInListIfExists == -1){
                                obsListRequests.add(requestInTable);
                            }
                            else{
                                obsListRequests.set(requestIndxInListIfExists, requestInTable);
                            }});
                        }
                    }
                });
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int checkIfRequestExist(Integer requestId) {
        for(UserRequestPresenter currentRequest: this.obsListRequests){
            if(requestId == currentRequest.getRequestId()){
                return this.obsListRequests.indexOf(currentRequest);
            }
        }

        return -1;
    }

    @FXML
    private void selectedComboBoxSimulation(){

    }
    @FXML
    private void onButtonSubmit(){
        int ticksValue = -1, secondsValue = -1;
        boolean freeChoice = false;
        if(this.comboBoxSimulations.getValue() == null){
            this.labelMsg.setText("Please choose a simulation from the list");
        }
        else if(!this.checkBoxFreeChoice.isSelected() && !this.checkBoxSeconds.isSelected() && !this.checkBoxTicks.isSelected()){
            this.labelMsg.setText("Please choose a termination for the simulation");
        }
        else if((this.checkBoxFreeChoice.isSelected() && this.checkBoxTicks.isSelected()) || (this.checkBoxFreeChoice.isSelected() && this.checkBoxSeconds.isSelected())){
            this.labelMsg.setText("Cannot choose a free choice and a ticks/seconds terminations simultaneously");
        }
        else if(this.checkBoxSeconds.isSelected() && (this.textFieldSeconds.getText().equals("") || !isNumber(this.textFieldSeconds.getText()))){
            this.labelMsg.setText("Please fill a valid value for seconds termination");
        }
        else if(this.checkBoxTicks.isSelected() && (this.textFieldTicks.getText().equals("") || !isNumber(this.textFieldTicks.getText()))){
            this.labelMsg.setText("Please fill a valid value for ticks termination");
        }
        else if(this.textFieldNumberOfRuns.getText().equals("") || !isNumber(this.textFieldNumberOfRuns.getText())){
            this.labelMsg.setText("Please fill a valid value for number of runs");
        }
        else{
            if(this.checkBoxTicks.isSelected()){
                ticksValue = Integer.parseInt(this.textFieldTicks.getText());
            }
            if(this.checkBoxSeconds.isSelected()){
                secondsValue = Integer.parseInt(this.textFieldSeconds.getText());
            }
            if(this.checkBoxFreeChoice.isSelected()){
                freeChoice = true;
            }
            String userName = Main.getUserName();
            int requestedRuns = Integer.parseInt(this.textFieldNumberOfRuns.getText());
            String simulationName = this.comboBoxSimulations.getValue();
            DtoTerminationForRequest termination = new DtoTerminationForRequest(secondsValue, ticksValue, freeChoice);
            DtoRequestForAdmin requestForAdminDto = new DtoRequestForAdmin(userName, simulationName, requestedRuns, "pending", termination);
            String jsonPayload = this.gson.toJson(requestForAdminDto);
            RequestBody requestBody = RequestBody.create(jsonPayload, null);
            Request requestForAdmin = new Request.Builder().url(Main.getBaseUrl() + "/user/sendRequestToAdmin").put(requestBody).build();
            Call call = this.client.newCall(requestForAdmin);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Platform.runLater(()->{
                        labelMsg.setText("Request was sent to admin succesfully");
                        clearAllRequestComponents();
                    });
                }
            });
        }
    }
    @FXML
    private void onButtonExecute(){
        if(this.currentSelectedRequestFromTable == null){
            this.labelMsg.setText("Please select a simulation from the table");
        }
        else{
            if(this.currentSelectedRequestFromTable.getFinishedRuns() == this.currentSelectedRequestFromTable.getRequestedRuns()){
                this.labelMsg.setText("The following request finished all it's requested runs");
            }
            else if((this.currentSelectedRequestFromTable.getRequestStatus().equalsIgnoreCase("pending")) ||
                    this.currentSelectedRequestFromTable.getRequestStatus().equalsIgnoreCase("rejected")){
                this.labelMsg.setText("Cannot execute a simulation that has a pending/rejected status");
            }
            else{

                this.userMenuController.switchToExecuteTab(this.currentSelectedRequestFromTable.getRequestId(), this.currentSelectedRequestFromTable.getSimulationName());
            }
        }
    }
    @FXML
    private void selectedTableViewItem(){
        if(this.tableViewRequests.getSelectionModel().getSelectedItem() != null){
            this.currentSelectedRequestFromTable = this.tableViewRequests.getSelectionModel().getSelectedItem();
            this.buttonExecute.setDisable(false);
        }
        else{
            this.buttonExecute.setDisable(true);
        }
    }

    private void clearAllRequestComponents() {
        Platform.runLater(()->{
            this.textFieldSeconds.clear();
            this.textFieldTicks.clear();
            this.textFieldNumberOfRuns.clear();
            this.checkBoxSeconds.setSelected(false);
            this.checkBoxTicks.setSelected(false);
            this.checkBoxFreeChoice.setSelected(false);
        });
    }

    private boolean isNumber(String text){
        try{
            Integer.parseInt(text);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    public void setUserMenuController(UserMenuController userMenuController) {
        this.userMenuController = userMenuController;
    }
}
