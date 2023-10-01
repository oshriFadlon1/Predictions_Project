package dtos.admin;

public class DtoSimulationsRequest {
    private int requestId;
    private String simulationName;
    private String userName;
    private int requestedRuns;
    private String simulationTermination;
    private String requestStatus;
    private int currentRuns;
    private int finishedRuns;

    public DtoSimulationsRequest(int requestId, String simulationName, String userName, int requestedRuns, String simulationTermination, String requestStatus, int currentRuns, int finishedRuns) {
        this.requestId = requestId;
        this.simulationName = simulationName;
        this.userName = userName;
        this.requestedRuns = requestedRuns;
        this.simulationTermination = simulationTermination;
        this.requestStatus = requestStatus;
        this.currentRuns = currentRuns;
        this.finishedRuns = finishedRuns;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public String getUserName() {
        return userName;
    }

    public int getRequestedRuns() {
        return requestedRuns;
    }

    public String getSimulationTermination() {
        return simulationTermination;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public int getCurrentRuns() {
        return currentRuns;
    }

    public int getFinishedRuns() {
        return finishedRuns;
    }
}
