package controllers;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import okhttp3.OkHttpClient;
import org.example.Main;
import presenters.EntityPresenter;
import presenters.EnvironmentPresenter;
import presenters.SimulationPresenter;

public class ExecutionsHistoryController implements Initializable {
    private ObservableList<EntityPresenter> obsListEntity;
    private ObservableList<EnvironmentPresenter> obsListEnvironment;
    private EntityPresenter currentSelectedEntityValue;
    private EnvironmentPresenter currentSelectedEnvValue;
    private OkHttpClient client;
    private Gson gson;
    private Thread simulationFetcherThread;
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
        this.tableViewEntities.setItems(obsListEntity);
        this.tableViewEnvironments.setItems(obsListEnvironment);
        this.gson = new Gson();
        this.client = Main.getCLIENT();
        this.simulationFetcherThread = new Thread(this::fetchEndedSimulations);
    }

    private void fetchEndedSimulations() {
        while(true){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void selectedEnvironmentTableValue(){
        if(this.tableViewEnvironments.getSelectionModel().getSelectedItem() != null){
            this.currentSelectedEnvValue = this.tableViewEnvironments.getSelectionModel().getSelectedItem();
            
        }
    }

    @FXML
    private void selectedEntityTableValue(){
        
    }
}
