package presenters;

public class SimulationPresenter {
    private String simulationName;
    private Integer simulationId;

    public String getSimulationName() {
        return simulationName;
    }

    public Integer getSimulationId() {
        return simulationId;
    }

    public SimulationPresenter(String simulationName, Integer simulationId) {
        this.simulationName = simulationName;
        this.simulationId = simulationId;
    }
}
