package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dtos.DtoCountTickPopulation;
import dtos.DtoHistogramInfo;
import dtos.admin.DtoFinalSimulationsDetails;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import okhttp3.*;
import org.example.Main;
import org.jetbrains.annotations.NotNull;
import presenters.*;

public class ExecutionsHistoryController implements Initializable {
    private ObservableList<EntityPresenter> obsListEntity;
    private ObservableList<EnvironmentPresenter> obsListEnvironment;
    private ObservableList<String> obsListEntityNames;
    private ObservableList<String> obsListPropertyNames;
    private ObservableList<SimulationPresenter> obsListSimulations;
    private ObservableList<HistogramPresenter> obsListHistogram;
    private SimulationPresenter currentSelectedSimulation;
    private OkHttpClient client;
    private Gson gson;
    private Thread simulationFetcherThread;
    @FXML
    private HBox hboxFinalDetails;
    @FXML
    private Label labelAvgPropertyValue;
    @FXML
    private Label labelAvgTickValue;
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
    private TableColumn<HistogramPresenter, String> columnPropertyVal;
    @FXML
    private TableColumn<HistogramPresenter, Integer> columnPropertyCount;

    @FXML
    private ListView<SimulationPresenter> listViewSimulations;

    @FXML
    private TableView<EntityPresenter> tableViewEntities;

    @FXML
    private TableView<EnvironmentPresenter> tableViewEnvironments;
    @FXML
    private TableView<HistogramPresenter> tableViewHistogramInfo;
    @FXML
    private Label labelRequestId;

    @FXML
    private Label labelSimulationId;

    @FXML
    private Label labelSimulationName;

    @FXML
    private Label labelSimulationUsername;
    @FXML
    private BarChart<String, Integer> barChartPopulation;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.obsListEntity = FXCollections.observableArrayList();
        this.obsListHistogram = FXCollections.observableArrayList();
        this.obsListSimulations = FXCollections.observableArrayList();
        this.obsListEnvironment = FXCollections.observableArrayList();
        this.obsListEntityNames = FXCollections.observableArrayList();
        this.obsListPropertyNames = FXCollections.observableArrayList();
        this.listViewSimulations.setCellFactory(param -> new CustomCellItem());
        this.columnEntityName.setCellValueFactory(new PropertyValueFactory<>("entityName"));
        this.columnEnvName.setCellValueFactory(new PropertyValueFactory<>("envName"));
        this.columnEnvValue.setCellValueFactory(new PropertyValueFactory<>("envValue"));
        this.columnPopulationEnd.setCellValueFactory(new PropertyValueFactory<>("endPopulation"));
        this.columnPopulationStart.setCellValueFactory(new PropertyValueFactory<>("startPopulation"));
        this.columnPropertyVal.setCellValueFactory(new PropertyValueFactory<>("propertyValue"));
        this.columnPropertyCount.setCellValueFactory(new PropertyValueFactory<>("countOfProperty"));
        this.tableViewEntities.setItems(obsListEntity);
        this.tableViewHistogramInfo.setItems(obsListHistogram);
        this.tableViewEnvironments.setItems(obsListEnvironment);
        this.comboBoxEntityProperty.setItems(obsListPropertyNames);
        this.comboBoxEntityName.setItems(obsListEntityNames);
        this.listViewSimulations.setItems(obsListSimulations);
        this.gson = new Gson();
        this.client = Main.getCLIENT();
        this.simulationFetcherThread = new Thread(this::fetchEndedSimulations);
        this.hboxFinalDetails.setVisible(false);
        this.simulationFetcherThread.start();
    }

    private void fetchEndedSimulations() {
        //TODO continue implementing the function
        while(true){
            try {
                Request simulationFetchRequest = new Request.Builder().url("admin/fetchAllEndedSimulations").build();
                Call call = this.client.newCall(simulationFetchRequest);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        List<DtoFinalSimulationsDetails> finalSimulationsDetails = gson.fromJson(response.body().string(), new TypeToken<List<DtoFinalSimulationsDetails>>() {}.getType());
                        for(DtoFinalSimulationsDetails currSimulation: finalSimulationsDetails){
                            SimulationPresenter simulationToCheck = checkIfSimulationExists(currSimulation.getSimulationId());
                            if(simulationToCheck == null){
                                Platform.runLater(()->{
                                    obsListSimulations.add(new SimulationPresenter(currSimulation.getSimulationName(), currSimulation.getSimulationId()));
                                });

                            }
                        }
                    }
                });
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private SimulationPresenter checkIfSimulationExists(int simulationId) {
        for(SimulationPresenter currSimulation: this.obsListSimulations){
            if(simulationId == currSimulation.getSimulationId()){
                return currSimulation;
            }
        }
        return null;
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
        fillComboBoxEntities(dtoFinalSimulationsDetails.getMapEntityToEndPopulation().keySet());
        labelRequestId.setText(String.valueOf(dtoFinalSimulationsDetails.getRequestNumber()));
        labelSimulationId.setText(String.valueOf(dtoFinalSimulationsDetails.getSimulationId()));
        labelSimulationName.setText(dtoFinalSimulationsDetails.getSimulationName());
        labelSimulationUsername.setText(dtoFinalSimulationsDetails.getUserName());
        initializeBarChart();
    }

    private void fillComboBoxEntities(Set<String> entityNames) {
        for(String currEntityName: entityNames){
            this.obsListEntityNames.add(currEntityName);
        }
    }

    @FXML
    private void selectedComboBoxEntityName(){
        this.obsListPropertyNames.clear();
        String selectedItem = this.comboBoxEntityName.getValue();
        if(selectedItem != null) {
            comboBoxEntityProperty.setDisable(false);
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Main.getBaseUrl() + "admin/fetchAllProperties").newBuilder();
            urlBuilder.addQueryParameter("simulationId", String.valueOf(this.currentSelectedSimulation.getSimulationId()));
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
                    List<String> allProperties = gson.fromJson(response.body().string(), new TypeToken<List<String>>() {}.getType());
                    Platform.runLater(()->{
                        for(String currentPropertyName: allProperties){
                            obsListPropertyNames.add(currentPropertyName);
                        }
                    });
                }
            });
        }

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

    private void initializeBarChart() {
        this.barChartPopulation.getData().clear();
        Request barChartRequest = new Request.Builder().url("admin/fetchBarChartDetails?simulationId=" + this.currentSelectedSimulation.getSimulationId()).build();
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
                        barChartPopulation.getData().addAll(seriesEntity1, seriesEntity2);
                    }
                    else {
                        barChartPopulation.getData().addAll(seriesEntity1);
                    }
            }
        });

    }

}
