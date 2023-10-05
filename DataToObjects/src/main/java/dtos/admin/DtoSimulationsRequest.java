package dtos.admin;

import dtos.requestInfo.DtoTerminationSimulationInfo;

public class DtoSimulationsRequest {
    private int requestId;
    private String simulationName;
    private String userName;
    private int requestedRuns;
    private DtoTerminationSimulationInfo simulationTermination;
    private String requestStatus;
    private int currentRuns;
    private int finishedRuns;

    public DtoSimulationsRequest(int requestId,
                                 String simulationName,
                                 String userName,
                                 int requestedRuns,
                                 DtoTerminationSimulationInfo simulationTermination,
                                 String requestStatus,
                                 int currentRuns,
                                 int finishedRuns) {
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

    public DtoTerminationSimulationInfo getSimulationTermination() {
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
