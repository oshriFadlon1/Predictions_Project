package dtos;

import java.util.Map;

public class DtoSimulationDetails {
    private int entity1Population;
    private int entity2Population;
    private int entity1StartPopulation;
    private int entity2StartPopulation;
    private String entity1Name;
    private String entity2Name;
    private int simulationTick;
    private long simulationTimePassed;
    private boolean isSimulationFinished;
    private boolean isSimulationPaused;
    private int simulationId;
    private Map<String, Object> envToValueMap;

    public DtoSimulationDetails(int entity1Population,
                                int entity2Population,
                                int entity1StartPopulation,
                                int entity2StartPopulation,
                                String entity1Name,
                                String entity2Name,
                                int simulationTick,
                                long simulationTimePassed,
                                boolean isSimulationFinished,
                                boolean isSimulationPaused,
                                int simulationId,
                                Map<String, Object> envToValueMap) {
        this.entity1Population = entity1Population;
        this.entity2Population = entity2Population;
        this.entity1StartPopulation = entity1StartPopulation;
        this.entity2StartPopulation = entity2StartPopulation;
        this.entity1Name = entity1Name;
        this.entity2Name = entity2Name;
        this.simulationTick = simulationTick;
        this.simulationTimePassed = simulationTimePassed;
        this.isSimulationFinished = isSimulationFinished;
        this.isSimulationPaused = isSimulationPaused;
        this.simulationId = simulationId;
        this.envToValueMap = envToValueMap;
    }

    public Map<String, Object> getEnvToValueMap() {
        return envToValueMap;
    }

    public int getSimulationId() {
        return simulationId;
    }

    public int getEntity1Population() {
        return entity1Population;
    }

    public int getEntity2Population() {
        return entity2Population;
    }


    public int getSimulationTick() {
        return simulationTick;
    }


    public long getSimulationTimePassed() {
        return simulationTimePassed;
    }

    public boolean isSimulationFinished() {
        return isSimulationFinished;
    }

    public String getEntity1Name() {
        return entity1Name;
    }

    public String getEntity2Name() {
        return entity2Name;
    }

    public boolean isSimulationPaused() {
        return isSimulationPaused;
    }

    public int getEntity1StartPopulation() {
        return entity1StartPopulation;
    }

    public int getEntity2StartPopulation() {
        return entity2StartPopulation;
    }
}
