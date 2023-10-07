package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dtos.DtoAllSimulationDetails;
import dtos.DtoCountTickPopulation;
import dtos.DtoHistogramInfo;
import dtos.DtoSimulationDetails;
import enums.SimulationState;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import okhttp3.*;
import org.example.Main;
import org.jetbrains.annotations.NotNull;
import presenters.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ResultsController implements Initializable {
    private OkHttpClient client;
    private Gson gson;
    private UserMenuController userMenuController;
    private DtoSimulationDetails simulationDetailsResult;
    private SimulationPresenter currSimulationPresenter;
    private ObservableList<EntityPresenter> obsListEntities;
    private ObservableList<SimulationPresenter> obsListSimulations;
    private ObservableList<String> obsListEntityNames;
    private ObservableList<String> obsListPropertyNames;
    private ObservableList<HistogramPresenter> obsListHistogram;
    private ObservableList<EnvironmentPresenter> obsListEnvironments;
    private Thread fetchSimulationsByUserNameThread;
    @FXML
    private Label labelAvgPropertyValue;

    @FXML
    private Label labelAvgTickValue;

    @FXML
    private BarChart<String, Integer> barchartPopulation;

    @FXML
    private Button buttonPause;

    @FXML
    private Button buttonRerun;

    @FXML
    private Button buttonResume;

    @FXML
    private Button buttonStop;

    @FXML
    private TableColumn<HistogramPresenter, Integer> columnCount;

    @FXML
    private TableColumn<EntityPresenter, Integer> columnCurrPopulation;

    @FXML
    private TableColumn<EntityPresenter, String> columnEntity;

    @FXML
    private TableColumn<EnvironmentPresenter, String> columnEnvName;

    @FXML
    private TableColumn<EnvironmentPresenter, Object> columnEnvValue;

    @FXML
    private TableColumn<EntityPresenter, Integer> columnStartPopulation;

    @FXML
    private TableColumn<HistogramPresenter, String> columnValue;

    @FXML
    private ComboBox<String> comboBoxEntityName;

    @FXML
    private ComboBox<String> comboBoxEntityProperty;

    @FXML
    private HBox hboxFinalDetails;

    @FXML
    private Label labelCurrTick;

    @FXML
    private Label labelCurrTimer;

    @FXML
    private Label labelIdSimulation;

    @FXML
    private Label labelSimulationId;

    @FXML
    private Label labelSimulationStatus;

    @FXML
    private ListView<SimulationPresenter> listViewSimulations;

    @FXML
    private TableView<EntityPresenter> tableEntities;

    @FXML
    private TableView<EnvironmentPresenter> tableEnvironments;

    @FXML
    private TableView<HistogramPresenter> tableHistogram;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.obsListEntities = FXCollections.observableArrayList();
        this.obsListEnvironments = FXCollections.observableArrayList();
        this.obsListSimulations = FXCollections.observableArrayList();
        this.obsListEntityNames = FXCollections.observableArrayList();
        this.obsListPropertyNames = FXCollections.observableArrayList();
        this.obsListHistogram = FXCollections.observableArrayList();
        this.columnEntity.setCellValueFactory(new PropertyValueFactory<>("entityName"));
        this.columnStartPopulation.setCellValueFactory(new PropertyValueFactory<>("startPopulation"));
        this.columnCurrPopulation.setCellValueFactory(new PropertyValueFactory<>("currPopulation"));
        this.columnValue.setCellValueFactory(new PropertyValueFactory<>("propertyValue"));
        this.columnCount.setCellValueFactory(new PropertyValueFactory<>("countOfProperty"));
        this.columnEnvName.setCellValueFactory(new PropertyValueFactory<>("environmentName"));
        this.columnEnvValue.setCellValueFactory(new PropertyValueFactory<>("environmentVal"));
        this.tableEntities.setItems(this.obsListEntities);
        this.listViewSimulations.setItems(this.obsListSimulations);
        this.tableHistogram.setItems(this.obsListHistogram);
        this.tableEnvironments.setItems(this.obsListEnvironments);
        this.listViewSimulations.setCellFactory(param -> new CustomItemCell());
        this.comboBoxEntityName.setItems(this.obsListEntityNames);
        this.comboBoxEntityProperty.setItems(this.obsListPropertyNames);
        this.hboxFinalDetails.setVisible(false);
        this.buttonRerun.setDisable(true);
        this.buttonPause.setDisable(true);
        this.buttonResume.setDisable(true);
        this.buttonStop.setDisable(true);
        this.client = Main.getClient();
        this.gson = new Gson();
        this.fetchSimulationsByUserNameThread = new Thread(this::fetchSimulationsByUserName);
        this.fetchSimulationsByUserNameThread.start();
    }

    private void fetchSimulationsByUserName(){
        while(true){
            Request fetchSimulationsRequest = new Request.Builder().url(Main.getBaseUrl() + "user/fetchSimulationsByUserName?userName=" + Main.getUserName()).build();
            Call call = this.client.newCall(fetchSimulationsRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    DtoAllSimulationDetails allCurrentSimulations = gson.fromJson(response.body().string(), DtoAllSimulationDetails.class);
                    Map<Integer, SimulationState> simulationToIsRunningMap = allCurrentSimulations.getMapOfAllSimulations();
                    for(int currId: simulationToIsRunningMap.keySet()) {
                        SimulationPresenter simulationToCheck = checkIfSimulationExists(currId);
                        if (simulationToCheck == null) {
                            SimulationPresenter currSimulationToAdd = new SimulationPresenter(currId, simulationToIsRunningMap.get(currId).toString());
                            obsListSimulations.add(currSimulationToAdd);
                        } else {
                            if (simulationToIsRunningMap.get(currId).toString().equalsIgnoreCase(SimulationState.FINISHED.toString())) {
                                simulationToCheck.setSimulationState(SimulationState.FINISHED.toString());
                            }
                            else if (simulationToIsRunningMap.get(currId).toString().equalsIgnoreCase(SimulationState.WAITING.toString())) {
                                simulationToCheck.setSimulationState(SimulationState.WAITING.toString());
                            }
                            else if (simulationToIsRunningMap.get(currId).toString().equalsIgnoreCase(SimulationState.RUNNING.toString())) {
                                simulationToCheck.setSimulationState(SimulationState.RUNNING.toString());
                            }
                            obsListSimulations.set(currId - 1, simulationToCheck);
                        }
                    }
                    List<DtoSimulationDetails> allSimulationsList = gson.fromJson(response.body().string(), new TypeToken<List<DtoSimulationDetails>>() {}.getType());

                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public SimulationPresenter checkIfSimulationExists(int simulationId){
        for(SimulationPresenter currSimulation: this.obsListSimulations){
            if(simulationId == currSimulation.getSimulationId()){
                return currSimulation;
            }
        }
        return null;
    }

// TODO rerun
    @FXML
    void ReRunSimulation(ActionEvent event) {

    }

    @FXML
    void onPausePressed(ActionEvent event) {
        if(this.currSimulationPresenter != null) {
            RequestBody requestBody = RequestBody.create("", null);
            Request pauseSimulationRequest = new Request.Builder().url(Main.getBaseUrl() + "user/pauseSimulation?simulationId=" + this.currSimulationPresenter.getSimulationId()).put(requestBody).build();
            Call call = this.client.newCall(pauseSimulationRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Platform.runLater(()->{
                        buttonPause.setDisable(true);
                        buttonResume.setDisable(false);
                        buttonStop.setDisable(false);
                    });
                }
            });
        }
    }

    @FXML
    void onResumePressed(ActionEvent event) {
        if(this.currSimulationPresenter != null){
            RequestBody requestBody = RequestBody.create("", null);
            Request resumeSimulationRequest = new Request.Builder().url(Main.getBaseUrl() + "user/resumeSimulation?simulationId=" + this.currSimulationPresenter.getSimulationId()).put((requestBody)).build();
            Call call = this.client.newCall(resumeSimulationRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Platform.runLater(()->{
                        buttonResume.setDisable(true);
                        buttonPause.setDisable(false);
                        buttonStop.setDisable(false);
                    });

                }
            });
        }
    }

    @FXML
    void onSelectedComboBoxEntitiesItem(ActionEvent event) {
        this.obsListPropertyNames.clear();
        String selectedItem = this.comboBoxEntityName.getValue();
        if(selectedItem != null) {
            int simulationId = this.currSimulationPresenter.getSimulationId();
            this.comboBoxEntityProperty.setDisable(false);
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Main.getBaseUrl() + "user/fetchAllProperties").newBuilder();
            urlBuilder.addQueryParameter("simulationId", String.valueOf(this.currSimulationPresenter.getSimulationId()));
            urlBuilder.addQueryParameter("entityName", this.comboBoxEntityName.getValue());
            HttpUrl url = urlBuilder.build();
            Request allEntityPropertiesRequest = new Request.Builder().url(url).build();
            Call call = this.client.newCall(allEntityPropertiesRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    List<String> allPropertyNames = gson.fromJson(response.body().string(), new TypeToken<List<String>>() {}.getType());
                    Platform.runLater(()->{
                        for(String currentPropertyName: allPropertyNames){
                            obsListPropertyNames.add(currentPropertyName);
                        }
                    });
                }
            });
        }
    }

    @FXML
    void onSelectedComboBoxPropertyItem(ActionEvent event) {
        this.obsListHistogram.clear();
        this.labelAvgTickValue.setText("");
        this.labelAvgPropertyValue.setText("");
        String selectedItemProperty = this.comboBoxEntityProperty.getValue();
        if(selectedItemProperty != null){
            int simulationId = this.currSimulationPresenter.getSimulationId();
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Main.getBaseUrl() + "user/fetchHistogramInfo").newBuilder();
            urlBuilder.addQueryParameter("simulationId", String.valueOf(this.currSimulationPresenter.getSimulationId()));
            urlBuilder.addQueryParameter("entityName", this.comboBoxEntityName.getValue());
            urlBuilder.addQueryParameter("propertyName", this.comboBoxEntityProperty.getValue());
            HttpUrl url = urlBuilder.build();
            Request histogramFetcherRequest = new Request.Builder().url(url).build();
            Call call = this.client.newCall(histogramFetcherRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    DtoHistogramInfo histogramInfo = gson.fromJson(response.body().string(), DtoHistogramInfo.class);
                    Platform.runLater(()->{
                        if(histogramInfo.getAvgInFinalPopulation() != -1){
                            Platform.runLater(()->{
                                labelAvgPropertyValue.setText(String.valueOf(histogramInfo.getAvgInFinalPopulation()));
                            });
                        }
                        else{
                            labelAvgPropertyValue.setText("The property is not a float value\n or the population is 0");
                        }

                        if(histogramInfo.getAvgChangeInTicks() != -1){
                            labelAvgTickValue.setText(String.valueOf(histogramInfo.getAvgChangeInTicks()));
                        }
                        else{
                            labelAvgTickValue.setText("The population is 0");
                        }
                    });

                    for(String str: histogramInfo.getValue2Count().keySet()){
                        Integer countOfProperty = histogramInfo.getValue2Count().get(str);
                        Platform.runLater(()->{
                            obsListHistogram.add(new HistogramPresenter(str, countOfProperty));
                        });
                    }
                }
            });
        }
    }


    @FXML
    void onStopPressed(ActionEvent event) {
        if(this.currSimulationPresenter != null) {
            RequestBody requestBody = RequestBody.create("", null);
            Request stopSimulationRequest = new Request.Builder().url(Main.getBaseUrl() + "user/stopSimulation?simulationId=" + this.currSimulationPresenter.getSimulationId()).put(requestBody).build();
            Call call = this.client.newCall(stopSimulationRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Platform.runLater(()->{
                        buttonResume.setDisable(true);
                        buttonStop.setDisable(true);
                        buttonPause.setDisable(true);
                        buttonRerun.setDisable(false);
                    });
                }
            });
        }
    }

    @FXML
    void selectedItem(MouseEvent event) {
        this.obsListEntityNames.clear();
        this.obsListPropertyNames.clear();
        this.obsListHistogram.clear();
        this.comboBoxEntityProperty.setDisable(true);
        if(this.listViewSimulations.getSelectionModel().getSelectedItem() != null) {
            this.currSimulationPresenter = this.listViewSimulations.getSelectionModel().getSelectedItem();
            fetchDetailsForCurrentSimulation();
            buttonRerun.setDisable(false);
            buttonPause.setDisable(false);
            buttonResume.setDisable(false);
            buttonStop.setDisable(false);
            if (this.simulationDetailsResult != null && !this.simulationDetailsResult.isSimulationFinished()) {
                hboxFinalDetails.setVisible(false);
                Thread bringDetailsThread = new Thread(() -> {
                    while (this.currSimulationPresenter == this.listViewSimulations.getSelectionModel().getSelectedItem()) {
                        Platform.runLater(this::presentSelectedSimulationInfo);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        fetchDetailsForCurrentSimulation();
                    }
                });
            } else {
                handleSimulationAfterFinish();
                presentSelectedSimulationInfo();
            }
        }
    }

    private void handleSimulationAfterFinish() {
        this.obsListEntityNames.add(this.simulationDetailsResult.getEntity1Name());
        if(!this.simulationDetailsResult.getEntity2Name().equalsIgnoreCase("")){
            this.obsListEntityNames.add(this.simulationDetailsResult.getEntity2Name());
        }
        this.hboxFinalDetails.setVisible(true);
        initializeBarChart();
    }

    private void initializeBarChart() {
        this.barchartPopulation.getData().clear();
        Request barChartRequest = new Request.Builder().url(Main.getBaseUrl() + "/user/fetchBarChartDetails?simulationId=" + this.currSimulationPresenter.getSimulationId()).build();
        Call call = this.client.newCall(barChartRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                List<DtoCountTickPopulation> countTickPopulationList = gson.fromJson(response.body().string(), new TypeToken<List<DtoCountTickPopulation>>() {}.getType());
                int ratio = countTickPopulationList.size() / 50;
                if (ratio <= 0){
                    ratio = 1;
                }
                int count = 0;
                boolean isSecondEntityFound = false;
                XYChart.Series<String, Integer> seriesEntity1 = new XYChart.Series();
                XYChart.Series<String, Integer> seriesEntity2 = new XYChart.Series();
                for (DtoCountTickPopulation dtoCountTickPopulation : countTickPopulationList){
                    if (count % ratio == 0 ){
                        isSecondEntityFound = true;
                        // we have 2 entities
                        if (dtoCountTickPopulation.getPopulationEntity2() != -1){
                            seriesEntity1.setName(dtoCountTickPopulation.getEntity1Name());
                            seriesEntity1.getData().add(new XYChart.Data<>(String.valueOf(dtoCountTickPopulation.getCurrentTick()), dtoCountTickPopulation.getPopulationEntity1()));
                            seriesEntity2.setName(dtoCountTickPopulation.getEntity2Name());
                            seriesEntity2.getData().add(new XYChart.Data<>(String.valueOf(dtoCountTickPopulation.getCurrentTick()), dtoCountTickPopulation.getPopulationEntity2()));
                        }
                        else{
                            seriesEntity1.setName(dtoCountTickPopulation.getEntity1Name());
                            seriesEntity1.getData().add(new XYChart.Data<>(String.valueOf(dtoCountTickPopulation.getCurrentTick()), dtoCountTickPopulation.getPopulationEntity1()));
                        }
                    }
                    count++;
                }
                if (isSecondEntityFound){
                    barchartPopulation.getData().addAll(seriesEntity1, seriesEntity2);
                }
                else {
                   barchartPopulation.getData().addAll(seriesEntity1);
                }
            }
        });
    }

    private void presentSelectedSimulationInfo() {
        int populationEntity1 = this.simulationDetailsResult.getEntity1Population();
        int startPopulationEntity1 = this.simulationDetailsResult.getEntity1StartPopulation();
        int populationEntity2 = this.simulationDetailsResult.getEntity2Population();
        int startPopulationEntity2 = this.simulationDetailsResult.getEntity2StartPopulation();
        this.labelCurrTick.setText(Integer.toString(this.simulationDetailsResult.getSimulationTick()));
        this.labelCurrTimer.setText(Long.toString(this.simulationDetailsResult.getSimulationTimePassed()));
        this.labelIdSimulation.setText(Integer.toString(this.simulationDetailsResult.getSimulationId()));
        this.labelSimulationStatus.setText(getModeOfCurrentSimulation(this.simulationDetailsResult));
        this.obsListEntities.clear();
        this.obsListEnvironments.clear();
        for(String envName: this.simulationDetailsResult.getEnvToValueMap().keySet()){
            Object envValue = this.simulationDetailsResult.getEnvToValueMap().get(envName);
            this.obsListEnvironments.add(new EnvironmentPresenter(envName, envValue));
        }
        this.obsListEntities.add(new EntityPresenter(this.simulationDetailsResult.getEntity1Name(), startPopulationEntity1, populationEntity1));
        if (populationEntity2 != -1 && !this.simulationDetailsResult.getEntity2Name().equalsIgnoreCase("")) {
            this.obsListEntities.add(new EntityPresenter(this.simulationDetailsResult.getEntity2Name(),startPopulationEntity2 , populationEntity2));
        }
        this.buttonRerun.setDisable(!this.simulationDetailsResult.isSimulationFinished());
    }

    private void fetchDetailsForCurrentSimulation(){
        Request fetchDetailsRequest =new Request.Builder().url(Main.getBaseUrl() + "/user/fetchSimulation?simulationId=" + this.currSimulationPresenter.getSimulationId()).build();
        Call call = this.client.newCall(fetchDetailsRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                DtoSimulationDetails currentDetailsForSimulations = gson.fromJson(response.body().string(), DtoSimulationDetails.class);
                simulationDetailsResult = currentDetailsForSimulations;
            }
        });
    }

    private String getModeOfCurrentSimulation(DtoSimulationDetails currentDetails) {
        if (currentDetails.isSimulationFinished()){
            return "Finished";
        }
        else if (!currentDetails.isSimulationPaused()){
            return "Running";
        }
        else{
            return "Waiting";
        }
    }
}
