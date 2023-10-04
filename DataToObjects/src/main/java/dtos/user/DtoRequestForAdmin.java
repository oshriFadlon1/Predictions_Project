package dtos.user;

public class DtoRequestForAdmin {
    private String userName;
    private String simulationName;
    private int requestedRuns;
    private String requestStatus;
    private DtoTerminationForRequest termination;

    public DtoRequestForAdmin(String userName, String simulationName, int requestedRuns, String requestStatus, DtoTerminationForRequest termination) {
        this.userName = userName;
        this.simulationName = simulationName;
        this.requestedRuns = requestedRuns;
        this.requestStatus = requestStatus;
        this.termination = termination;
    }

    public String getUserName() {
        return userName;
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

    public DtoTerminationForRequest getTermination() {
        return termination;
    }
}
