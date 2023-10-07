package controllers;

import com.google.gson.reflect.TypeToken;
import adminComponent.controller.EntityComponentController;
import adminComponent.controller.EnvironmentComponentController;
import adminComponent.controller.GridComponentController;
import adminComponent.controller.RuleComponentController;
import dtos.*;
import dtos.detailsPreview.DetailsPreviewinfo.DetailPreviewSelected;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

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
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
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
            labelLoadStatus.setText(responseBody);
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
                Platform.runLater(() -> setTreeViewFromWorld());
            }
        });
    }

    private void setTreeViewFromWorld() {
        TreeItem<String> rootItem = treeViewSimulation.getRoot();
        rootItem.getChildren().clear();

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

        DetailPreviewSelected detailPreviewSelected = findTheDtoPreview(selectedItem);
        try {
            // case choose general (termination and grid) selectedItem.getValue().equalsIgnoreCase(generalInfo)
            if (detailPreviewSelected.getCountLevels() == 3){
                loadAndAddFXML("/scenes/detailsComponent/GridComponent.fxml", generalInfo);
                gridComponentController.updateLabelTerm(detailPreviewSelected.getSelectedDtoResponsePreview().getRow(), detailPreviewSelected.getSelectedDtoResponsePreview().getCol());
                return;
            }

            // case choose environment property. selectedItem.getParent().getValue().equalsIgnoreCase(environment)
            if (detailPreviewSelected.getCountLevels() == 4){
                loadAndAddFXML("/scenes/detailsComponent/EnvironmentComponent.fxml", environment);
                environmentComponentController.updateLabelEnv(detailPreviewSelected.getSelectedDtoResponsePreview().getDtoEnvironments().get(selectedItem.getValue()));
                return;
            }

            if (detailPreviewSelected.getCountLevels() == 5){

                if (isEntityPropertySelected(selectedItem, detailPreviewSelected)){
                    //TODO find the entity and fine the property the user choice.
                    // case choose entity property. selectedItem.getParent().getParent().getValue().equalsIgnoreCase(entities)
                    loadAndAddFXML("/scenes/detailsComponent/EntityComponent.fxml", "Entity");
                    this.entityComponentController.updateLabelEnt(getSelectedPropertyEntity(selectedItem, detailPreviewSelected));
                    return;
                }
                else {
                    // case choose rule action
                    //TODO find the rule and fine the action the user choice. selectedItem.getParent().getParent().getValue().equalsIgnoreCase(rules)
                    loadAndAddFXML("/scenes/detailsComponent/RuleComponent.fxml", "Rule");
                    this.ruleComponentController.updateLabelRule(getSelectedAction(selectedItem, detailPreviewSelected));
                }

            }
        } catch (Exception ignore){}
    }

    private DtoActionResponse getSelectedAction(TreeItem<String> selectedItem, DetailPreviewSelected detailPreviewSelected) {
        String ruleName = selectedItem.getParent().getValue();
        DtoResponseRules responseRules = null;
        for (DtoResponseRules dtoResponseRules : detailPreviewSelected.getSelectedDtoResponsePreview().getDtoResponseRules()) {
            if (dtoResponseRules.getRuleName().equalsIgnoreCase(ruleName)){
                responseRules = dtoResponseRules;
                break;
            }
        }
        DtoActionResponse response = null;
        for (DtoActionResponse dtoActionResponse:responseRules.getActionNames()) {
            if (selectedItem.getValue().equalsIgnoreCase(dtoActionResponse.getActionName())){
                response = dtoActionResponse;
                break;
            }
        }
        return response;
    }

    private DtoPropertyDetail getSelectedPropertyEntity(TreeItem<String> selectedItem, DetailPreviewSelected detailPreviewSelected) {
        String entityName = selectedItem.getParent().getValue();
        DtoEntitiesDetail entitiesDetail  = null;

        entitiesDetail = detailPreviewSelected.getSelectedDtoResponsePreview().getDtoResponseEntities().get(entityName);

        DtoPropertyDetail response = null;
        for (DtoPropertyDetail property : entitiesDetail.getPropertyDefinitionEntityList()) {
            if (selectedItem.getValue().equalsIgnoreCase(property.getPropertyName())){
                response = property;
                break;
            }
        }
        return response;
    }


    private boolean isEntityPropertySelected(TreeItem<String> selectedItem, DetailPreviewSelected detailPreviewSelected) {
        String nameOfTheSelectedItem = selectedItem.getParent().getParent().getValue();
        return !nameOfTheSelectedItem.equalsIgnoreCase(rules);
    }

    private DetailPreviewSelected findTheDtoPreview(TreeItem<String> selectedItem) {
        int count = 1;

        DtoResponsePreview responsePreview = null;
        TreeItem<String> treeItem = selectedItem;
        // get the depth of the selected item
        while (treeItem.getValue().equalsIgnoreCase(simulations)){
            count ++;
            treeItem = treeItem.getParent();
        }

        // get the DtoPreview we need.
        treeItem = selectedItem;
        DtoResponsePreview dtoResponsePreview = null;
        String SimulationName = null;
        while(treeItem.getParent().getValue().equalsIgnoreCase(simulations)){
            SimulationName = treeItem.getValue();
        }

        for (DtoResponsePreview preview:this.worldPreview) {
            if (preview.getSimulationName().equalsIgnoreCase(SimulationName)){
                dtoResponsePreview = preview;
                break;
            }
        }
        return new DetailPreviewSelected(dtoResponsePreview, count);
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
