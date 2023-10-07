package presenters;

public class SimulationPresenter {
    private int simulationId;
    private String simulationState;

    public SimulationPresenter(int simulationId, String simulationStates) {
        this.simulationId = simulationId;
        this.simulationState = simulationStates;
    }

    public int getSimulationId() {
        return simulationId;
    }
    public String getSimulationState() {
        return simulationState;
    }
    public void setSimulationState(String simulationState) {
        this.simulationState = simulationState;
    }
}
