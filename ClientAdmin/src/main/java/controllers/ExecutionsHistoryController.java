package controllers;

import com.google.gson.Gson;
import dtos.DtoHistogramInfo;
import dtos.admin.DtoFinalSimulationsDetails;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import okhttp3.*;
import org.example.Main;
import org.jetbrains.annotations.NotNull;
import presenters.EntityPresenter;
import presenters.EnvironmentPresenter;
import presenters.SimulationPresenter;

public class ExecutionsHistoryController implements Initializable {
    private ObservableList<EntityPresenter> obsListEntity;
    private ObservableList<EnvironmentPresenter> obsListEnvironment;
    private ObservableList<String> obsListEntityNames;
    private ObservableList<String> obsListPropertyNames;
    private EntityPresenter currentSelectedEntityValue;
    private EnvironmentPresenter currentSelectedEnvValue;
    private SimulationPresenter currentSelectedSimulation;
    private OkHttpClient client;
    private Gson gson;
    private Thread simulationFetcherThread;
    @FXML
    private HBox hboxFinalDetails;
    @FXML
    private ComboBox<String> comboBoxEntityProperty;
    @FXML
    private ComboBox<String> comboBoxEntityName;
    @FXML
    private TableColumn<EntityPresenter, String> columnEntityName;

    @FXML
    private TableColumn<EnvironmentPresenter, String> columnEnvName;

    @FXML
    private TableColumn<EnvironmentPresenter, Object> columnEnvValue;

    @FXML
    private TableColumn<EntityPresenter, Integer> columnPopulationEnd;

    @FXML
    private TableColumn<EntityPresenter, Integer> columnPopulationStart;

    @FXML
    private ListView<SimulationPresenter> listViewSimulations;

    @FXML
    private TableView<EntityPresenter> tableViewEntities;

    @FXML
    private TableView<EnvironmentPresenter> tableViewEnvironments;
    @FXML
    private Label labelRequestId;

    @FXML
    private Label labelSimulationId;

    @FXML
    private Label labelSimulationName;

    @FXML
    private Label labelSimulationUsername;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.obsListEntity = FXCollections.observableArrayList();
        this.obsListEnvironment = FXCollections.observableArrayList();
        this.obsListEntityNames = FXCollections.observableArrayList();
        this.obsListPropertyNames = FXCollections.observableArrayList();
        this.tableViewEntities.setItems(obsListEntity);
        this.tableViewEnvironments.setItems(obsListEnvironment);
        this.comboBoxEntityProperty.setItems(obsListPropertyNames);
        this.comboBoxEntityName.setItems(obsListEntityNames);
        this.gson = new Gson();
        this.client = Main.getCLIENT();
        this.simulationFetcherThread = new Thread(this::fetchEndedSimulations);
        this.hboxFinalDetails.setVisible(false);
    }

    private void fetchEndedSimulations() {
        //TODO continue implementing the function
        while(true){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @FXML
    private void selectedListViewValue(){
        if(this.listViewSimulations.getSelectionModel().getSelectedItem() != null){
            this.currentSelectedSimulation = this.listViewSimulations.getSelectionModel().getSelectedItem();
            Request simulationsDetailsRequest = new Request.Builder().url(Main.getBaseUrl() + "admin/fetchFinalSimulationDetails?simulationId=" + this.currentSelectedSimulation.getSimulationId()).build();
            Call call = this.client.newCall(simulationsDetailsRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    DtoFinalSimulationsDetails dtoFinalSimulationsDetails = gson.fromJson(response.body().string(), DtoFinalSimulationsDetails.class);
                    Platform.runLater(()->{
                        handleFinalSimulationDetails(dtoFinalSimulationsDetails);
                    });
                }
            });
        }
    }

    private void handleFinalSimulationDetails(DtoFinalSimulationsDetails dtoFinalSimulationsDetails) {
        hboxFinalDetails.setVisible(true);
        comboBoxEntityProperty.setDisable(true);
        this.obsListEnvironment.clear();
        this.obsListEntity.clear();
        this.obsListEntityNames.clear();
        this.obsListPropertyNames.clear();
        fillEntityTable(dtoFinalSimulationsDetails.getMapEntityToStartPopulation(), dtoFinalSimulationsDetails.getMapEntityToEndPopulation());
        fillEnvTable(dtoFinalSimulationsDetails.getMapEnvToValue());
        labelRequestId.setText(String.valueOf(dtoFinalSimulationsDetails.getRequestNumber()));
        labelSimulationId.setText(String.valueOf(dtoFinalSimulationsDetails.getSimulationId()));
        labelSimulationName.setText(dtoFinalSimulationsDetails.getSimulationName());
        labelSimulationUsername.setText(dtoFinalSimulationsDetails.getUserName());

    }

    @FXML
    private void selectedComboBoxEntityName(){
        comboBoxEntityProperty.setDisable(false);

    }
    @FXML
    private void selectedComboBoxEntityProperty(){
        if(this.comboBoxEntityProperty.getValue() != null) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Main.getBaseUrl() + "admin/fetchHistogramInfo").newBuilder();
            urlBuilder.addQueryParameter("simulationId", String.valueOf(this.currentSelectedSimulation.getSimulationId()));
            urlBuilder.addQueryParameter("entityName", this.comboBoxEntityName.getValue());
            urlBuilder.addQueryParameter("propertyName", this.comboBoxEntityProperty.getValue());
            HttpUrl url = urlBuilder.build();
            Request histogramRequest = new Request.Builder().url(url).build();
            Call call = this.client.newCall(histogramRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    DtoHistogramInfo histogramInfo = gson.fromJson(response.body().string(), DtoHistogramInfo.class);

                }
            });
        }
    }

    private void fillEnvTable(Map<String, Object> mapEnvToValue) {
        for(String envName: mapEnvToValue.keySet()){
            this.obsListEnvironment.add(new EnvironmentPresenter(envName, mapEnvToValue.get(envName)));
        }
    }

    private void fillEntityTable(Map<String, Integer> mapEntityToStartPopulation, Map<String, Integer> mapEntityToEndPopulation) {
        for(String entityName: mapEntityToEndPopulation.keySet()){
            this.obsListEntity.add(new EntityPresenter(entityName, mapEntityToStartPopulation.get(entityName), mapEntityToEndPopulation.get(entityName)));
        }
    }

}
