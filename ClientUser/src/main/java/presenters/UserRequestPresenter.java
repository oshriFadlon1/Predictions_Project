package presenters;

public class UserRequestPresenter {
    private int requestId;
    private String simulationName;
    private int requestedRuns;
    private String requestStatus;
    private int currentRuns;
    private int simulationsFinished;

    public UserRequestPresenter(String simulationName, int requestedRuns, String requestStatus, int currentRuns, int simulationsFinished) {
        this.simulationName = simulationName;
        this.requestedRuns = requestedRuns;
        this.requestStatus = requestStatus;
        this.currentRuns = currentRuns;
        this.simulationsFinished = simulationsFinished;
        requestId++;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public int getRequestedRuns() {
        return requestedRuns;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public int getCurrentRuns() {
        return currentRuns;
    }

    public int getFinishedRuns() {
        return simulationsFinished;
    }
}
