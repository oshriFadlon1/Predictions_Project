package dtos.admin;

import java.util.Map;

public class DtoFinalSimulationsDetails {
    private int simulationId;
    private String userName;
    private String simulationName;
    private int requestNumber;
    private Map<String, Integer> mapEntityToStartPopulation;
    private Map<String, Integer> mapEntityToEndPopulation;
    private Map<String, Object> mapEnvToValue;

    public DtoFinalSimulationsDetails(int simulationId,
                                      String userName,
                                      String simulationName,
                                      int requestNumber,
                                      Map<String, Integer> mapEntityToStartPopulation,
                                      Map<String, Integer> mapEntityToEndPopulation,
                                      Map<String, Object> mapEnvToValue) {
        this.simulationId = simulationId;
        this.userName = userName;
        this.simulationName = simulationName;
        this.requestNumber = requestNumber;
        this.mapEntityToStartPopulation = mapEntityToStartPopulation;
        this.mapEntityToEndPopulation = mapEntityToEndPopulation;
        this.mapEnvToValue = mapEnvToValue;
    }

    public int getSimulationId() {
        return simulationId;
    }

    public String getUserName() {
        return userName;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public Map<String, Integer> getMapEntityToStartPopulation() {
        return mapEntityToStartPopulation;
    }

    public Map<String, Integer> getMapEntityToEndPopulation() {
        return mapEntityToEndPopulation;
    }

    public Map<String, Object> getMapEnvToValue() {
        return mapEnvToValue;
    }
}
