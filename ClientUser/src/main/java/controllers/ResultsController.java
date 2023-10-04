package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ResultsController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    private Label avgPropertyValue;

    @FXML
    private Label avgTickValue;

    @FXML
    private BarChart<?, ?> barchartPopulation;

    @FXML
    private Button buttonPause;

    @FXML
    private Button buttonRerun;

    @FXML
    private Button buttonResume;

    @FXML
    private Button buttonStop;

    @FXML
    private TableColumn<?, ?> columnCount;

    @FXML
    private TableColumn<?, ?> columnValue;

    @FXML
    private ComboBox<?> comboBoxEntityName;

    @FXML
    private ComboBox<?> comboBoxEntityProperty;

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
    private ListView<?> listViewSimulations;

    @FXML
    private TableColumn<?, ?> tableColumnEntity;

    @FXML
    private TableColumn<?, ?> tableColumnPopulation;

    @FXML
    private TableView<?> tableViewEntities;

    @FXML
    private TableView<?> tableViewHistogram;

    @FXML
    void ReRunSimulation(ActionEvent event) {

    }

    @FXML
    void onPausePressed(ActionEvent event) {

    }

    @FXML
    void onResumePressed(ActionEvent event) {

    }

    @FXML
    void onSelectedComboBoxEntitiesItem(ActionEvent event) {

    }

    @FXML
    void onSelectedComboBoxPropertyItem(ActionEvent event) {

    }

    @FXML
    void onStopPressed(ActionEvent event) {

    }

    @FXML
    void selectedItem(MouseEvent event) {

    }
}
