package controllers;

import com.google.gson.Gson;
import dtos.admin.DtoAllSimulationRequests;
import dtos.admin.DtoSimulationsRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.*;
import org.example.Main;
import org.jetbrains.annotations.NotNull;
import presenters.SimulationRequestsPresenter;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;

public class AllocationsController implements Initializable {
    private ObservableList<SimulationRequestsPresenter> obsListSimulationRequests;
    private Gson gson = new Gson();
    private OkHttpClient client;
    private Thread requestsFetcherThread;
    private SimulationRequestsPresenter currentSelectedSimulationRequest;
    @FXML
    private Button buttonApprove;
    @FXML
    private Button buttonReject;
    @FXML
    private TableColumn<SimulationRequestsPresenter, Integer> columnCurrentRuns;

    @FXML
    private TableColumn<SimulationRequestsPresenter, Integer> columnFinishedRuns;

    @FXML
    private TableColumn<SimulationRequestsPresenter, Integer> columnRequestId;

    @FXML
    private TableColumn<SimulationRequestsPresenter, Integer> columnRequestedRuns;

    @FXML
    private TableColumn<SimulationRequestsPresenter, String> columnSimulationName;

    @FXML
    private TableColumn<SimulationRequestsPresenter, String> columnRequestStatus;

    @FXML
    private TableColumn<SimulationRequestsPresenter, String> columnTermination;

    @FXML
    private TableColumn<SimulationRequestsPresenter, String> columnUserName;

    @FXML
    private TableView<SimulationRequestsPresenter> tableViewRequests;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.client = Main.getCLIENT();
        this.obsListSimulationRequests = FXCollections.observableArrayList();
        this.columnCurrentRuns.setCellValueFactory(new PropertyValueFactory<>("currentRuns"));
        this.columnFinishedRuns.setCellValueFactory(new PropertyValueFactory<>("finishedRuns"));
        this.columnRequestId.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        this.columnRequestedRuns.setCellValueFactory(new PropertyValueFactory<>("requestedRuns"));
        this.columnSimulationName.setCellValueFactory(new PropertyValueFactory<>("simulationName"));
        this.columnRequestStatus.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));
        this.columnTermination.setCellValueFactory(new PropertyValueFactory<>("simulationTermination"));
        this.columnUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        this.tableViewRequests.setItems(this.obsListSimulationRequests);
        this.requestsFetcherThread = new Thread(this::fetchRequests);
    }

    private void fetchRequests(){
        while(true){
            Request requestsFetch = new Request.Builder().url(Main.getBaseUrl() + "/admin/fetchAllRequests").build();
            Call call = this.client.newCall(requestsFetch);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    DtoAllSimulationRequests dtoAllSimulationRequests = gson.fromJson(response.body().string(), DtoAllSimulationRequests.class);
                    Map<Integer, DtoSimulationsRequest> allSimulationRequests = dtoAllSimulationRequests.getDtoSimulationsRequests();
                    for(int currRequestId: allSimulationRequests.keySet()) {
                        SimulationRequestsPresenter simulationRequestsPresenter = checkIfRequestExists(currRequestId);
                        if(simulationRequestsPresenter == null){
                            DtoSimulationsRequest dtoSimulationsRequest = allSimulationRequests.get(currRequestId);
                            simulationRequestsPresenter = new SimulationRequestsPresenter(currRequestId, dtoSimulationsRequest.getSimulationName(), dtoSimulationsRequest.getUserName(),
                                    dtoSimulationsRequest.getRequestedRuns(), dtoSimulationsRequest.getSimulationTermination(), dtoSimulationsRequest.getRequestStatus());
                            obsListSimulationRequests.add(simulationRequestsPresenter);
                        }
                        else{
                            obsListSimulationRequests.set(currRequestId - 1, simulationRequestsPresenter);
                        }
                    }
                }
            });
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private SimulationRequestsPresenter checkIfRequestExists(int currRequestId) {
        for(SimulationRequestsPresenter currRequest: this.obsListSimulationRequests){
            if(currRequestId == currRequest.getRequestId()){
                return currRequest;
            }
        }

        return null;
    }

    @FXML
    private void selectedTableValue(){
        if(this.tableViewRequests.getSelectionModel().getSelectedItem() != null){
            this.currentSelectedSimulationRequest = this.tableViewRequests.getSelectionModel().getSelectedItem();
            if(this.currentSelectedSimulationRequest.getRequestStatus().equalsIgnoreCase("pending")){
                this.buttonApprove.setDisable(false);
                this.buttonReject.setDisable(false);
            }
            else{
                this.buttonApprove.setDisable(true);
                this.buttonReject.setDisable(true);
            }
        }
        else{
            this.buttonApprove.setDisable(true);
            this.buttonReject.setDisable(true);
        }
    }

    @FXML
    private void onButtonApprove(){
        //TODO maybe create a dto of the table row
        DtoSimulationsRequest dtoSimulationsRequest = new DtoSimulationsRequest(this.currentSelectedSimulationRequest.getRequestId(), this.currentSelectedSimulationRequest.getSimulationName(),
                this.currentSelectedSimulationRequest.getUserName(), this.currentSelectedSimulationRequest.getRequestedRuns(), this.currentSelectedSimulationRequest.getSimulationTermination(),
                "approved", 0, 0);
        String jsonPayload = this.gson.toJson(dtoSimulationsRequest);
        RequestBody requestBody = RequestBody.create(jsonPayload, null);
        Request approveRequest = new Request.Builder().url(Main.getBaseUrl() + "/admin/approveRequest").put(requestBody).build();
        Call call = this.client.newCall(approveRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                currentSelectedSimulationRequest.setRequestStatus("approved");
                //TODO need to figure out what to do when the admin approves the request
            }
        });
    }
    @FXML
    private void onButtonReject(){
        DtoSimulationsRequest dtoSimulationsRequest = new DtoSimulationsRequest(this.currentSelectedSimulationRequest.getRequestId(), this.currentSelectedSimulationRequest.getSimulationName(),
                this.currentSelectedSimulationRequest.getUserName(), this.currentSelectedSimulationRequest.getRequestedRuns(), this.currentSelectedSimulationRequest.getSimulationTermination(),
                "rejected", -1, -1);
        String jsonPayload = this.gson.toJson(dtoSimulationsRequest);
        RequestBody requestBody = RequestBody.create(jsonPayload, null);
        Request rejectRequest = new Request.Builder().url(Main.getBaseUrl() + "/admin/rejectRequest").put(requestBody).build();
        Call call = this.client.newCall(rejectRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                currentSelectedSimulationRequest.setRequestStatus("denied");
                //TODO need to figure out what to do when the admin rejects the request
            }
        });
    }
}
