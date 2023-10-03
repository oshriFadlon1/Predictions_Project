package world;

import entity.EntityToPopulation;
import pointCoord.PointCoord;
import termination.Termination;

import java.time.LocalDateTime;
import java.util.List;

public class GeneralInformation {
    private PointCoord worldSize;
    private LocalDateTime startOfSimulationDate;
    private static int idOfSimulation = 0;
    private Termination termination;
    private boolean isSimulationDone = false;
    private boolean isSimulationPaused = false;
    private List<EntityToPopulation> entitiesToPopulations;
    private String userName;
    private String simulationName;
    private int requestNumber;

    public GeneralInformation(PointCoord worldSize,
                              LocalDateTime startOfSimulationDate,
                              Termination termination,
                              List<EntityToPopulation> entitiesToPopulations,
                              String userName,
                              String simulationName,
                              int requestNumber) {
        this.isSimulationPaused = false;
        this.worldSize = worldSize;
        this.startOfSimulationDate = startOfSimulationDate;
        this.termination = termination;
        this.isSimulationDone = false;
        this.entitiesToPopulations = entitiesToPopulations;
        this.userName = userName;
        this.simulationName = simulationName;
        this.requestNumber = requestNumber;
        idOfSimulation++;
    }

    public List<EntityToPopulation> getEntitiesToPopulations() {
        return entitiesToPopulations;
    }

    public void setEntitiesToPopulations(List<EntityToPopulation> entitiesToPopulations) {
        this.entitiesToPopulations = entitiesToPopulations;
    }

    public PointCoord getWorldSize() {
        return worldSize;
    }

    public void setWorldSize(PointCoord worldSize) {
        this.worldSize = worldSize;
    }

    public LocalDateTime getStartOfSimulationDate() {
        return startOfSimulationDate;
    }

    public void setStartOfSimulationDate(LocalDateTime startOfSimulationDate) {
        this.startOfSimulationDate = startOfSimulationDate;
    }

    public static int getIdOfSimulation() {
        return idOfSimulation;
    }

    public static void setIdOfSimulation(int idOfSimulation) {
        GeneralInformation.idOfSimulation = idOfSimulation;
    }

    public Termination getTermination() {
        return termination;
    }

    public void setTermination(Termination termination) {
        this.termination = termination;
    }

    public boolean isSimulationDone() {
        return isSimulationDone;
    }

    public void setSimulationDone(boolean simulationDone) {
        isSimulationDone = simulationDone;
    }

    public boolean isSimulationPaused() {
        return isSimulationPaused;
    }

    public void setSimulationPaused(boolean simulationPaused) {
        isSimulationPaused = simulationPaused;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSimulationName() {
        return simulationName;
    }

    public void setSimulationName(String simulationName) {
        this.simulationName = simulationName;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    @Override
    public String toString() {
        return "GeneralInformation{" +
                "worldSize=" + worldSize +
                ", startOfSimulationDate=" + startOfSimulationDate +
                ", termination=" + termination +
                ", isSimulationDone=" + isSimulationDone +
                ", isSimulationPaused=" + isSimulationPaused +
                ", entitiesToPopulations=" + entitiesToPopulations +
                ", userName='" + userName + '\'' +
                ", simulationName='" + simulationName + '\'' +
                ", requestNumber=" + requestNumber +
                '}';
    }
}
