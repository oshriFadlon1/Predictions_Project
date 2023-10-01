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

    private PointCoord worldSize;

    private String worldName;


    public WorldDefinition(Map<String, EnvironmentDefinition> allEnvironments, List<EntityDefinition> entityDefinitions, List<Rule> rules) {
        this.allEnvironments = allEnvironments;
        this.entityDefinitions = entityDefinitions;
        Rules = rules;
    }

    public WorldDefinition(String worldName) {
        this.allEnvironments = new HashMap<>();
        this.entityDefinitions = new ArrayList<>();
        this.Rules = new ArrayList<>();
        this.worldName = worldName;
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

    public void setPointCoord(PointCoord coordsOfWorld) {
        this.worldSize = coordsOfWorld;
    }

    public PointCoord getWorldSize() {
        return worldSize;
    }

    public void setWorldSize(PointCoord worldSize) {
        this.worldSize = worldSize;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }
}
