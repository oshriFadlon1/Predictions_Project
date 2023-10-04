package com.example.server.engine;

import allocationManager.AllocationManager;
import com.example.server.engineDtos.DtoResponseToController;
import constans.Constans;
import dtos.*;
import dtos.admin.DtoFinalSimulationsDetails;
import entity.EntityDefinition;
import environment.EnvironmentDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import property.*;
import rule.ActivationForRule;
import rule.Rule;
import rule.action.IAction;
import simulationmanager.SimulationExecutionerManager;
import utility.Utilities;
import world.WorldDefinition;
import exceptions.GeneralException;
import xmlParser.XmlParser;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

@Component
public class MainEngine {

    private XmlParser xmlParser;
    private WorldDefinition xmlWorldDefinition;
    private Map<String, WorldDefinition> worldDefinitionForSimulation;
    private SimulationExecutionerManager simulationExecutionerManager;
    private DtoBasicSimulationInfo dtoAllSimulationsStartingInfo; // get the basic info for reRun a given simulation
    private AllocationManager allocationManager;


    public MainEngine() {
        // set basic number of threads to by 1
        this.simulationExecutionerManager = new SimulationExecutionerManager(1);
        this.worldDefinitionForSimulation = new HashMap<>();
        this.dtoAllSimulationsStartingInfo = null;
        this.xmlWorldDefinition = null;
        this.xmlParser = new XmlParser();
        this.allocationManager = new AllocationManager();
    }

    public List<DtoResponsePreview> previewWorldsInfo(){
        List<DtoResponsePreview> worldsPreviewInfo = new ArrayList<>();
        for (String name:this.worldDefinitionForSimulation.keySet()) {
            WorldDefinition worldDefinition = this.worldDefinitionForSimulation.get(name);
            worldsPreviewInfo.add(new DtoResponsePreview(name,
                    getEnvironmentsInfo(worldDefinition),
                    getEntitiesInfoSimulation(worldDefinition),
                    getRulesInfoSimulation(worldDefinition),
                    worldDefinition.getWorldSize().getRow(),
                    worldDefinition.getWorldSize().getCol()));
        }
        return worldsPreviewInfo;
    }

    public void clearAllInformation(){
        if(this.simulationExecutionerManager != null) {
            this.simulationExecutionerManager.clearInformation();
        }
    }

    public List<String> bringPropertiesByEntityName(int simulationId, String entityName){
        List<String> propertyNamesList = this.simulationExecutionerManager.bringPropertyNamesList(simulationId, entityName);
        return propertyNamesList;
    }

    public DtoHistogramInfo fetchInfoOnChosenProperty(int simulationId, String entityName, String propertyName) {
        return this.simulationExecutionerManager.fetchInfoOnChosenProperty(simulationId, entityName, propertyName);
    }

    private Map<String, DtoEnvironmentDetails> getEnvironmentsInfo(WorldDefinition worldDefinition) {
        Map<String, DtoEnvironmentDetails> name2EnvironmentDetails = new HashMap<>();
        for (String name:worldDefinition.getAllEnvironments().keySet()) {
            EnvironmentDefinition environmentDefinition = worldDefinition.getAllEnvironments().get(name);
            if (environmentDefinition.getEnvPropertyDefinition().getPropertyRange() == null)
            {
                name2EnvironmentDetails.put(name,
                        new DtoEnvironmentDetails(environmentDefinition.getEnvPropertyDefinition().getPropertyName(),
                        environmentDefinition.getEnvPropertyDefinition().getPropertyType(),
                        -1,-1));
            } else {
                name2EnvironmentDetails.put(name,
                        new DtoEnvironmentDetails(environmentDefinition.getEnvPropertyDefinition().getPropertyName(),
                                environmentDefinition.getEnvPropertyDefinition().getPropertyType(),
                                environmentDefinition.getEnvPropertyDefinition().getPropertyRange().getFrom(),
                                environmentDefinition.getEnvPropertyDefinition().getPropertyRange().getTo()));
            }
        }
        return name2EnvironmentDetails;
    }

    private Map<String, DtoEntitiesDetail> getEntitiesInfoSimulation(WorldDefinition worldDefinition) {
        Map<String, DtoEntitiesDetail> entities = new HashMap<>();
        String nameEntity = "";
        boolean isTheFirst = true;
        for (EntityDefinition entityDefinition: worldDefinition.getEntityDefinitions()) {
            List<DtoPropertyDetail> propertyDefinitionEntityList = new ArrayList<>();
            for (String key:entityDefinition.getPropertyDefinition().keySet()) {
                PropertyDefinitionEntity propertyDefinitionEntity = entityDefinition.getPropertyDefinition().get(key);
                if (propertyDefinitionEntity.getPropertyDefinition().getPropertyRange() == null){
                    propertyDefinitionEntityList.add(new DtoPropertyDetail(
                            propertyDefinitionEntity.getPropertyDefinition().getPropertyName(),
                            propertyDefinitionEntity.getPropertyDefinition().getPropertyType(),
                            -1,
                            -1,
                            propertyDefinitionEntity.getPropValue().getRandomInit(),
                            propertyDefinitionEntity.getPropValue().getInit()));
                } else {
                    propertyDefinitionEntityList.add(new DtoPropertyDetail(
                            propertyDefinitionEntity.getPropertyDefinition().getPropertyName(),
                            propertyDefinitionEntity.getPropertyDefinition().getPropertyType(),
                            propertyDefinitionEntity.getPropertyDefinition().getPropertyRange().getFrom(),
                            propertyDefinitionEntity.getPropertyDefinition().getPropertyRange().getTo(),
                            propertyDefinitionEntity.getPropValue().getRandomInit(),
                            propertyDefinitionEntity.getPropValue().getInit()));
                }
            }
            entities.put(entityDefinition.getEntityName(),new DtoEntitiesDetail(entityDefinition.getEntityName(), propertyDefinitionEntityList));
        }
        return entities;
    }
    private List<DtoResponseRules> getRulesInfoSimulation(WorldDefinition worldDefinition) {
        List<DtoResponseRules> dtoResponseRules = new ArrayList<>();
        List<DtoActionResponse> ActionName;
        String ruleName = "";
        ActivationForRule activation = null;
        for (Rule rule: worldDefinition.getRules()) {
            ActionName = new ArrayList<>();
            for (IAction action:rule.getActions()) {
                ActionName.add(action.getActionResponse());
            }
            dtoResponseRules.add(new DtoResponseRules(rule.getRuleName(),
                    rule.getActivation().getTicks(),
                    rule.getActivation().getProbability(),
                    ActionName));
        }
        return dtoResponseRules;
    }


    //func 1
    public DtoResponseToController addWorldDefinition(InputStream inputStream){
        try {
            this.xmlWorldDefinition =  this.xmlParser.tryToReadXml(inputStream);
            if (!this.worldDefinitionForSimulation.containsKey(this.xmlWorldDefinition.getWorldName())){
                this.worldDefinitionForSimulation.put(this.xmlWorldDefinition.getWorldName(), this.xmlWorldDefinition);
            }
            else {
                return new DtoResponseToController("The name of current XML world definition is already used. ", HttpStatus.CONFLICT, false);
            }
        }
        catch(JAXBException | IOException | GeneralException e){
            if(e instanceof  JAXBException || e instanceof IOException){
                return new DtoResponseToController("There was an error in reading the XML file.", HttpStatus.CONFLICT, false);
            }
            else{
                return new DtoResponseToController(((GeneralException) e).getErrorMsg(), HttpStatus.CONFLICT, false);
            }
        }

        return new DtoResponseToController(Constans.SUCCEED_LOAD_FILE, HttpStatus.CREATED ,true);
    }

    //func 2
    public List<DtoResponsePreview> showAllDefinitionSimulation(){
        return previewWorldsInfo();
    }

    //func 3
    public void executeSimulation(DtoUiToEngine envInputFromUser){
//        WorldDefinition worldDefinition = this.worldDefinitionForSimulation.get(envInputFromUser.getNameOfSimulation());
//        Map<String, Object> environmentsForEngine = envInputFromUser.getEnvironmentToValue();
//        List<EntityToPopulation> entitiesToPopulations = createEntitiesToPopulationList(envInputFromUser);
//        GeneralInformation infoForSimulation = new GeneralInformation(envInputFromUser.getPopulation1(), envInputFromUser.getPopulation2(),
//                this.worldDefinitionForSimulation.getWorldSize(), LocalDateTime.now() , worldDefinition.getTermination(), entitiesToPopulations);
//        Map<String, EnvironmentInstance> environmentInstancesMap = createAllEnvironmentInstances(environmentsForEngine);
//        WorldInstance worldInstance = new WorldInstance(environmentInstancesMap, this.worldDefinitionForSimulation.getEntityDefinitions(), this.worldDefinitionForSimulation.getRules(), infoForSimulation);
//        this.dtoAllSimulationsStartingInfo.addStartingSimulationDetails(envInputFromUser, GeneralInformation.getIdOfSimulation());
//        this.simulationExecutionerManager.addCurrentSimulationToManager(worldInstance);
    }

    public DtoSimulationDetails getSimulationById(int idOfCurrentSimulation) {
        return this.simulationExecutionerManager.getSimulationById(idOfCurrentSimulation);
    }

    public int getNumberOfSimulation() {
        return this.simulationExecutionerManager.getNumberOfSimulation();
    }

    public DtoUiToEngine getSimulationStartingInfoById(int idOfChosenSimulation){
        return this.dtoAllSimulationsStartingInfo.getChosenRerunSimulation(idOfChosenSimulation);
    }

    public List<DtoCountTickPopulation> getSimulationListOfPopulationPerTick(int simulationId) {
        return this.simulationExecutionerManager.getSimulationListOfPopulationPerTick(simulationId);
    }

    public DtoAllSimulationDetails getAllSimulations() {
        return this.simulationExecutionerManager.createMapOfSimulationsToIsRunning();
    }

    public void pauseCurrentSimulation(int simulationId) {
        this.simulationExecutionerManager.pauseCurrentSimulation(simulationId);
    }

    public void resumeCurretnSimulation(int simulationId) {
        this.simulationExecutionerManager.resumeCurrentSimulation(simulationId);
    }

    public void stopCurrentSimulation(int simulationId) {
        this.simulationExecutionerManager.stopCurrentSimulation(simulationId);
    }

    public DtoQueueManagerInfo getQueueManagerInfo() {
        if (this.simulationExecutionerManager != null){
            DtoQueueManagerInfo simulationsStateManager = this.simulationExecutionerManager.getQueueManagerInfo();
            return simulationsStateManager;
        }
        return null;
    }
//TODO RETURN TO WORK
//    private List<EntityToPopulation> createEntitiesToPopulationList(DtoUiToEngine inputFromUser) {
//        List<EntityToPopulation> entitiesToPopulationList = new ArrayList<>();
//        List<EntityDefinition> entityDefsList = this.worldDefinitionForSimulation.getEntityDefinitions();
//        for(EntityDefinition currEntityDefinition: entityDefsList){
//            EntityToPopulation newEntityPopulation;
//            if(currEntityDefinition.getEntityName().equalsIgnoreCase(inputFromUser.getPrimaryEntityName())){
//                newEntityPopulation = new EntityToPopulation(currEntityDefinition, inputFromUser.getPopulation1());
//                entitiesToPopulationList.add(newEntityPopulation);
//            }
//            else{
//                newEntityPopulation = new EntityToPopulation(currEntityDefinition, inputFromUser.getPopulation2());
//                entitiesToPopulationList.add(newEntityPopulation);
//            }
//        }
//        return entitiesToPopulationList;
//    }
//TODO RETURN TO WORK
//    private Map<String, EnvironmentInstance> createAllEnvironmentInstances(Map<String, Object> environmentsForEngine) {
//        Map<String, EnvironmentDefinition> allEnvDefs = this.worldDefinitionForSimulation.getAllEnvironments();
//        Map<String, EnvironmentInstance> allEnvIns = new HashMap<>();
//        for(String envName: environmentsForEngine.keySet()){
//            EnvironmentDefinition currEnvDef = allEnvDefs.get(envName);
//            allEnvIns.put(envName, new EnvironmentInstance(currEnvDef.createCloneOfEnvironmentDefinition(), environmentsForEngine.get(envName)));
//        }
//        return allEnvIns;
//    }

    public DtoSimulationDetails fetchChosenWorld(int userSimulationChoice) {
        DtoSimulationDetails currentChosenSimulation = this.simulationExecutionerManager.getSimulationById(userSimulationChoice);
        return currentChosenSimulation;
    }

    public Object initializeRandomEnvironmentValues(PropertyDefinition propertyDef){
        Object initVal = null;
        switch(propertyDef.getPropertyType().toLowerCase()){
            case "float":
                float initEnvValFloat = Utilities.initializeRandomFloat(propertyDef.getPropertyRange());
                initVal = initEnvValFloat;
                break;
            case "string":
                String initEnvValString = Utilities.initializeRandomString();
                initVal = initEnvValString;
                break;
            case "boolean":
                boolean initEnvValBoolean = Utilities.initializeRandomBoolean();
                initVal = initEnvValBoolean;
                break;
            default:
                break;
        }
        return initVal;
    }

    public boolean validateUserEnvInput(String userInput, PropertyDefinition propDef){
        switch(propDef.getPropertyType().toLowerCase()){
            case "float":
                if(!Utilities.isFloat(userInput)){
                    return false;
                }
                else{
                    float valueOFInputFloat = Float.parseFloat(userInput);
                    if(!(valueOFInputFloat >= propDef.getPropertyRange().getFrom() && valueOFInputFloat <= propDef.getPropertyRange().getTo())){
                        return false;
                    }
                }
                break;
            case "string":
                break;
            case "boolean":
                if(!userInput.equalsIgnoreCase("true")  && !userInput.equalsIgnoreCase("false")){
                    return false;
                }
                break;

        }

        return true;
    }
    public Object initializeValueFromUserInput(String userInput, PropertyDefinition propDef){
        Object initVal = null;
        switch(propDef.getPropertyType().toLowerCase()){
            case "float":
                float initFloat = Float.parseFloat(userInput);
                initVal = initFloat;
                break;
            case "string":
                String initString = userInput;
                initVal = initString;
                break;
            case "boolean":
                boolean initBoolean = Boolean.parseBoolean(userInput);
                initVal = initBoolean;

        }

        return initVal;
    }

    public DtoResponseToController updateNumberOfThread(int newNumberOfThread){
        this.simulationExecutionerManager.UpdateThreadPool(newNumberOfThread);
        return new DtoResponseToController("Successfully updated.", HttpStatus.OK, true);
    }

    public DtoFinalSimulationsDetails getFinalSimulationDetails(int simulationId) {
        return this.simulationExecutionerManager.getFinalSimulationDetails(simulationId);
    }

    public List<DtoFinalSimulationsDetails> fetchAllEndedSimulations() {
        return this.simulationExecutionerManager.fetchAllEndedSimulations();
    }
}
