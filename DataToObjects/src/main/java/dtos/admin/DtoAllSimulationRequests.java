package dtos.admin;

import java.util.List;
import java.util.Map;

public class DtoAllSimulationRequests {
    private Map<Integer, DtoSimulationsRequest> dtoSimulationsRequests;

    public DtoAllSimulationRequests(Map<Integer, DtoSimulationsRequest> dtoSimulationsRequests) {
        this.dtoSimulationsRequests = dtoSimulationsRequests;
    }

    public Map<Integer, DtoSimulationsRequest> getDtoSimulationsRequests() {
        return dtoSimulationsRequests;
    }
}
