package allocationManager;

import termination.Termination;

public class UserRequest {
    private int requestId;
    private String simulationName;
    private String userName;
    private int requestedRuns;
    private Termination termination;
    private String requestStatus;
    private int currentRuns;
    private int finishedRuns;

    public UserRequest(int requestId,
                       String simulationName,
                       String userName,
                       int requestedRuns,
                       Termination termination,
                       String requestStatus,
                       int currentRuns,
                       int finishedRuns) {
        this.requestId = requestId;
        this.simulationName = simulationName;
        this.userName = userName;
        this.requestedRuns = requestedRuns;
        this.termination = termination;
        this.requestStatus = requestStatus;
        this.currentRuns = currentRuns;
        this.finishedRuns = finishedRuns;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getRequestedRuns() {
        return requestedRuns;
    }

    public void setRequestedRuns(int requestedRuns) {
        this.requestedRuns = requestedRuns;
    }

    public Termination getTermination() {
        return termination;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public int getCurrentRuns() {
        return currentRuns;
    }

    public void setCurrentRuns(int currentRuns) {
        this.currentRuns = currentRuns;
    }

    public int getFinishedRuns() {
        return finishedRuns;
    }

    public void setFinishedRuns(int finishedRuns) {
        this.finishedRuns = finishedRuns;
    }
}
