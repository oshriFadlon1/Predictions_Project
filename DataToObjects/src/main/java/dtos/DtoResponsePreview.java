package dtos;

import java.util.List;
import java.util.Map;

public class DtoResponsePreview {

    private String simulationName;

    private Map<String, DtoEnvironmentDetails> dtoEnvironments;

    private Map<String, DtoEntitiesDetail> dtoResponseEntities;

    private List<DtoResponseRules> dtoResponseRules;

    private int row;

    private int col;

    public DtoResponsePreview(String simulationName,
                              Map<String, DtoEnvironmentDetails> dtoEnvironments,
                              Map<String, DtoEntitiesDetail> dtoResponseEntities,
                              List<DtoResponseRules> dtoResponseRules,
                              int row,
                              int col) {
        this.simulationName = simulationName;
        this.dtoEnvironments = dtoEnvironments;
        this.dtoResponseEntities = dtoResponseEntities;
        this.dtoResponseRules = dtoResponseRules;
        this.row = row;
        this.col = col;
    }

    public Map<String, DtoEnvironmentDetails> getDtoEnvironments() {
        return dtoEnvironments;
    }

    public void setDtoEnvironments(Map<String, DtoEnvironmentDetails> dtoEnvironments) {
        this.dtoEnvironments = dtoEnvironments;
    }

    public Map<String, DtoEntitiesDetail> getDtoResponseEntities() {
        return dtoResponseEntities;
    }

    public void setDtoResponseEntities(Map<String, DtoEntitiesDetail> dtoResponseEntities) {
        this.dtoResponseEntities = dtoResponseEntities;
    }

    public List<DtoResponseRules> getDtoResponseRules() {
        return dtoResponseRules;
    }

    public void setDtoResponseRules(List<DtoResponseRules> dtoResponseRules) {
        this.dtoResponseRules = dtoResponseRules;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    @Override
    public String toString() {
        return "DtoResponsePreview{" +
                "simulationName='" + simulationName + '\'' +
                ", dtoEnvironments=" + dtoEnvironments +
                ", dtoResponseEntities=" + dtoResponseEntities +
                ", dtoResponseRules=" + dtoResponseRules +
                ", row=" + row +
                ", col=" + col +
                '}';
    }
}
