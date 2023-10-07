package controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllers.detailComopnentController.EntityComponentController;
import controllers.detailComopnentController.EnvironmentComponentController;
import controllers.detailComopnentController.GridComponentController;
import controllers.detailComopnentController.RuleComponentController;
import dtos.*;
import dtos.detailsPreview.DetailsPreviewinfo.DetailPreviewSelected;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import okhttp3.*;
import org.example.Main;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DetailsController implements Initializable {

    private Gson gson;
    private OkHttpClient client;
    private List<DtoResponsePreview> responsePreviewList;
    private Thread fetchPreviewWorlds;

    private final String simulations = "Simulations";
    private final String environment = "Environment";
    private final String entities = "Entities";
    private final String generalInfo = "General";
    private final String rules = "Rules";

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private TreeView<String> treeView;

    private GridComponentController gridComponentController;
    private EntityComponentController entityComponentController;
    private EnvironmentComponentController environmentComponentController;
    private RuleComponentController ruleComponentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.fetchPreviewWorlds = new Thread(this::fetchPreviewSimulations);
        this.gson = new Gson();
        this.client = Main.getClient();

        TreeItem<String> rootItem = new TreeItem<>(simulations);
        treeView.setRoot(rootItem);
        treeView.setEditable(true);
        treeView.setShowRoot(false);
    }
    @FXML
    void SelectedItem(MouseEvent event) {
        TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
        // case choose world or not set the file yet.
        if (selectedItem == null || this.responsePreviewList == null || selectedItem.getValue().equalsIgnoreCase(simulations)){
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

        for (DtoResponsePreview preview:this.responsePreviewList) {
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

            mainAnchorPane.getChildren().setAll(component);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DtoResponsePreview fetchWorldPreviewBySimulationName(String simulationName) {
        DtoResponsePreview dtoResponsePreview = null;
        for (DtoResponsePreview responsePreview: this.responsePreviewList) {
            if (responsePreview.getSimulationName().equalsIgnoreCase(simulationName)){
                dtoResponsePreview = responsePreview;
            }
        }
        return dtoResponsePreview;
    }

    private void fetchPreviewSimulations() {
        while (true) {
            Request simulationFetchRequest = new Request.Builder().url(Main.getBaseUrl() + "/user/previewWorlds").build();
            Call call = this.client.newCall(simulationFetchRequest);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    List<DtoResponsePreview> responsePreviewList = gson.fromJson(response.body().string(), new TypeToken<List<DtoResponsePreview>>() {
                    }.getType());
                    if (CheckIfAddNewSimulation(responsePreviewList)){
                        setTreeViewFromWorld();
                    }
                }
            });
            try{
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean CheckIfAddNewSimulation(List<DtoResponsePreview> responsePreviewList){
        List<DtoResponsePreview> dtoResponsePreviews = new ArrayList<>();
        boolean copyExist = false;
        boolean foundNewWorld = false;
        for (DtoResponsePreview responsePreview : responsePreviewList) {
            copyExist = false;
            dtoResponsePreviews.add(responsePreview);
            for (DtoResponsePreview dtoResponsePreview : this.responsePreviewList) {
                if (dtoResponsePreview.getSimulationName().equalsIgnoreCase(responsePreview.getSimulationName())){
                    copyExist = true;
                    foundNewWorld = true;
                    break;
                }
            }
            if (!copyExist){
                dtoResponsePreviews.add(responsePreview);
            }
        }
        this.responsePreviewList = dtoResponsePreviews;
        return foundNewWorld;
    }

    private void setTreeViewFromWorld() {
        TreeItem<String> rootItem = treeView.getRoot();
        rootItem.getChildren().clear();

        for (DtoResponsePreview dtoResponsePreview: this.responsePreviewList) {
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

        treeView.setShowRoot(true);
    }
}
