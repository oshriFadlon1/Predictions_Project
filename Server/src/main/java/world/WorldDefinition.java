package world;

import entity.EntityDefinition;
import entity.EntityInstance;
import environment.EnvironmentDefinition;
import pointCoord.PointCoord;
import rule.Rule;
import termination.Termination;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldDefinition implements Serializable {
    private Map<String, EnvironmentDefinition> allEnvironments;

    private List<EntityDefinition> entityDefinitions;

    private List<Rule> Rules;

    private Termination termination;

    private PointCoord worldSize;

    private int numberOfThreads;


    public WorldDefinition(Map<String, EnvironmentDefinition> allEnvironments, List<EntityDefinition> entityDefinitions, List<Rule> rules, Termination termination) {
        this.allEnvironments = allEnvironments;
        this.entityDefinitions = entityDefinitions;
        Rules = rules;
        this.termination = termination;
    }

    public WorldDefinition(Termination termination) {
        this.allEnvironments = new HashMap<>();
        this.entityDefinitions = new ArrayList<>();
        Rules = new ArrayList<>();
        this.termination = termination;
    }

    public Map<String, EnvironmentDefinition> getAllEnvironments() {
        return allEnvironments;
    }

    public void setAllEnvironments(Map<String, EnvironmentDefinition> allEnvironments) {
        this.allEnvironments = allEnvironments;
    }

    public List<EntityDefinition> getEntityDefinitions() {
        return entityDefinitions;
    }

    public void setEntityDefinitions(List<EntityDefinition> entityDefinitions) {
        this.entityDefinitions = entityDefinitions;
    }

    public List<Rule> getRules() {
        return Rules;
    }

    public void setRules(List<Rule> rules) {
        Rules = rules;
    }

    public Termination getTermination() {
        return termination;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
    }

//    public void resetEntityDefinition(){
//        for (EntityDefinition entityDefinition : this.entityDefinitions) {
//            entityDefinition.setEndPopulation(entityDefinition.getStartPopulation());
//        }
//    }

    public void setPointCoord(PointCoord coordsOfWorld) {
        this.worldSize = coordsOfWorld;
    }

    public PointCoord getWorldSize() {
        return worldSize;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setThreadCount(int threadCount) {
        this.numberOfThreads = threadCount;
    }

//    public void setPopulation(int population1, int population2) {
//        int count = 0;
//        for (EntityDefinition entityDefinition : this.entityDefinitions) {
//            if (count == 0)
//            {
//                entityDefinition.setStartPopulation(population1);
//                entityDefinition.setEndPopulation(population1);
//                count++;
//            }
//            else if (count == 1){
//                entityDefinition.setStartPopulation(population2);
//                entityDefinition.setEndPopulation(population2);
//                count++;
//            }
//        }
//    }
}
