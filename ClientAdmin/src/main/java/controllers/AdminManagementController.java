package controllers;

import com.google.gson.reflect.TypeToken;
import component.controller.EntityComponentController;
import component.controller.EnvironmentComponentController;
import component.controller.GridComponentController;
import component.controller.RuleComponentController;
import dtos.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.*;
import org.example.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import presenters.SimulationPresenter;

public class AdminManagementController implements Initializable {
    private Gson gson;
    private OkHttpClient client;
    private Thread queueDataFetchingThread;
    @FXML
    private Button buttonLoadFile;
    @FXML
    private Button buttonThreadCount;
    @FXML
    private TextField textFieldThreadCount;
    @FXML
    private ScrollPane rootScrollPane;
    @FXML
    private Label labelThreadCountMsg;
    @FXML
    private Label labelLoadStatus;
    @FXML
    private Label labelSimulationsEnded;
    @FXML
    private Label labelSimulationsPending;
    @FXML
    private Label labelSimulationsProgress;

    @FXML
    private TreeView<String> treeViewSimulation;
    @FXML
    private AnchorPane anchorPaneLoadDetails;
    private List<DtoResponsePreview> worldPreview;
    private GridComponentController gridComponentController;
    private EntityComponentController entityComponentController;
    private EnvironmentComponentController environmentComponentController;
    private RuleComponentController ruleComponentController;

    private final String simulations = "Simulations";
    private final String environment = "Environment";
    private final String entities = "Entities";
    private final String generalInfo = "General";
    private final String rules = "Rules";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.queueDataFetchingThread = new Thread(this::fetchQueueData);
        this.queueDataFetchingThread.start();
        this.gson = new Gson();
        TreeItem<String> rootItem = new TreeItem<>(simulations);
        treeViewSimulation.setRoot(rootItem);
        treeViewSimulation.setEditable(true);
        treeViewSimulation.setShowRoot(false);
    }

    @FXML
    private void onLoadButton(){
        Stage stage = (Stage)rootScrollPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter xmlFilter = new FileChooser.ExtensionFilter("XML Files", "*.xml");
        fileChooser.getExtensionFilters().add(xmlFilter);
        File file =  fileChooser.showOpenDialog(stage);

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        Request request = new Request.Builder()
                .url(Main.getBaseUrl() + "/admin/upload")
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            // Handle the response here
            String responseBody = response.body().string();
            System.out.println("Response: " + responseBody);
            fetchPreviewSimulations();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void onButtonThreadCount(){
        try{
            String numberOfThreadAsString = this.textFieldThreadCount.getText();

            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody requestBody = RequestBody.create(mediaType, numberOfThreadAsString);
            Request threadCountRequest = new Request.Builder().url(Main.getBaseUrl() + "/admin/setThreadCount").patch(requestBody).build();

            Call call = this.client.newCall(threadCountRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Platform.runLater(() -> {
                        labelThreadCountMsg.setText("Updated thread count");
                        labelThreadCountMsg.setVisible(true);
                    });
                }
            });
        }
        catch (NumberFormatException e){
            this.labelThreadCountMsg.setVisible(true);
            this.labelThreadCountMsg.setText("Invalid thread count value");
        }

    }

    private void fetchQueueData(){
        if(this.client == null){
            this.client = Main.getCLIENT();
        }
        while(true){
        Request queueRequest = new Request.Builder().url(Main.getBaseUrl() + "/admin/getQueue").build();
        Call call = this.client.newCall(queueRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                DtoQueueManagerInfo queueManagerInfo = gson.fromJson(response.body().string(), DtoQueueManagerInfo.class);
                if (queueManagerInfo != null){
                    Platform.runLater(()->{
                        labelSimulationsEnded.setText(queueManagerInfo.getCountOfSimulationEnded());
                        labelSimulationsPending.setText(queueManagerInfo.getCountOfSimulationsPending());
                        labelSimulationsProgress.setText(queueManagerInfo.getCountOfSimulationInProgress());
                    });
                }
            }
        });
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fetchPreviewSimulations() {
        Request simulationFetchRequest = new Request.Builder().url(Main.getBaseUrl() + "/admin/previewWorlds").build();
        Call call = this.client.newCall(simulationFetchRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                worldPreview = gson.fromJson(response.body().string(), new TypeToken<List<DtoResponsePreview>>() {
                }.getType());
                setTreeViewFromWorld();
            }
        });
    }

    private void setTreeViewFromWorld() {
        TreeItem<String> rootItem = treeViewSimulation.getRoot();

        for (DtoResponsePreview dtoResponsePreview: this.worldPreview) {
            TreeItem<String> childName = new TreeItem<>(dtoResponsePreview.getSimulationName());

            // add environment details
            TreeItem<String> Env = new TreeItem<>(environment);
            for (String name : dtoResponsePreview.getDtoEnvironments().keySet()) {
                TreeItem<String> EnvName = new TreeItem<>(name);
                Env.getChildren().add(EnvName);
            }
            childName.getChildren().add(Env);

            // add Entities
            TreeItem<String> Ent = new TreeItem<>(entities);
            for (String name: dtoResponsePreview.getDtoResponseEntities().keySet()) {

                TreeItem<String> EntName = new TreeItem<>(dtoResponsePreview.getDtoResponseEntities().get(name).getEntityName());
                for (DtoPropertyDetail dtoPropertyDetail:dtoResponsePreview.getDtoResponseEntities().get(name).getPropertyDefinitionEntityList()) {
                    TreeItem<String> entPropName = new TreeItem<>(dtoPropertyDetail.getPropertyName());
                    EntName.getChildren().add(entPropName);
                }
                Ent.getChildren().add(EntName);
            }
            childName.getChildren().add(Ent);

            TreeItem<String> Rule = new TreeItem<>(rules);
            for (DtoResponseRules rule : dtoResponsePreview.getDtoResponseRules()) {
                int count = 1;
                TreeItem<String> RuleName = new TreeItem<>(rule.getRuleName());
                TreeItem<String> activation = new TreeItem<>("Ticks: " + rule.getTicks() + ", probability: " + rule.getProbability());
                for (DtoActionResponse actionResponse: rule.getActionNames()) {
                    TreeItem<String> actionName = new TreeItem<>(count + ") " + actionResponse.getActionName());
                    RuleName.getChildren().add(actionName);
                    count++;
                }
                RuleName.getChildren().add(activation);
                Rule.getChildren().add(RuleName);
            }
            childName.getChildren().add(Rule);

            TreeItem<String> general = new TreeItem<>(generalInfo);

            childName.getChildren().add(general);

            rootItem.getChildren().add(childName);
        }

        treeViewSimulation.setShowRoot(true);
    }

    @FXML
    void SelectedItem(MouseEvent event) {
        TreeItem<String> selectedItem = treeViewSimulation.getSelectionModel().getSelectedItem();
        // case choose world or not set the file yet.
        if (selectedItem == null || this.worldPreview == null || selectedItem.getValue().equalsIgnoreCase(simulations)){
            return;
        }

        DtoResponsePreview dtoResponsePreview = findTheDtoPreview(selectedItem);
        try {
            // case choose general (termination and grid)
            if (selectedItem.getValue().equalsIgnoreCase(generalInfo)){
                loadAndAddFXML("/ui/javaFx/scenes/sceneDetails/detailsComponents/TerminationComponent.fxml", generalInfo);
                gridComponentController.updateLabelTerm(dtoResponsePreview.getRow(), dtoResponsePreview.getCol());
                return;
            }

            // case choose environment property.
            if (selectedItem.getParent().getValue().equalsIgnoreCase(environment)){
                loadAndAddFXML("/ui/javaFx/scenes/sceneDetails/detailsComponents/EnvironmentComponent.fxml", environment);
                environmentComponentController.updateLabelEnv(dtoResponsePreview.getDtoEnvironments().get(selectedItem.getValue()));
                return;
            }

//            // case choose entity property.
//            if (selectedItem.getParent().getParent().getValue().equalsIgnoreCase(entities)){
//                loadAndAddFXML("/ui/javaFx/scenes/sceneDetails/detailsComponents/EntityComponent.fxml", "Entity");
//                entityComponentController.updateLabelEnt(getSelectedPropertyEntity(selectedItem));
//                return;
//            }
//            // case choose rule action
//            if (selectedItem.getParent().getParent().getValue().equalsIgnoreCase(rules)){
//                loadAndAddFXML("/ui/javaFx/scenes/sceneDetails/detailsComponents/RuleComponent.fxml", "Rule");
//                this.ruleComponentController.updateLabelRule(getSelectedAction(selectedItem));
//            }
        } catch (Exception ignore){}
    }

    private DtoResponsePreview findTheDtoPreview(TreeItem<String> selectedItem) {
        return null;
    }

    private void loadAndAddFXML(String fxmlFileName, String whichController) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL mainFXML = getClass().getResource(fxmlFileName);
            loader.setLocation(mainFXML);
            AnchorPane component = loader.load();
            switch (whichController.toLowerCase()){
                case "environment":
                    this.environmentComponentController = loader.getController();
                    break;
                case "entity":
                    this.entityComponentController = loader.getController();
                    break;
                case"rule":
                    this.ruleComponentController = loader.getController();
                    break;
                case "general":
                    this.gridComponentController = loader.getController();
                    break;
            }

            anchorPaneLoadDetails.getChildren().setAll(component);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
