package world;

import allocationManager.UserRequest;
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
    private UserRequest requestDetails;

    public GeneralInformation(PointCoord worldSize,
                              LocalDateTime startOfSimulationDate,
                              Termination termination,
                              List<EntityToPopulation> entitiesToPopulations,
                              UserRequest requestDetails) {
        this.isSimulationPaused = false;
        this.worldSize = worldSize;
        this.startOfSimulationDate = startOfSimulationDate;
        this.termination = termination;
        this.isSimulationDone = false;
        this.entitiesToPopulations = entitiesToPopulations;
        this.requestDetails = requestDetails;
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

    public UserRequest getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(UserRequest requestDetails) {
        this.requestDetails = requestDetails;
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
                ", requestDetails=" + requestDetails +
                '}';
    }
}
