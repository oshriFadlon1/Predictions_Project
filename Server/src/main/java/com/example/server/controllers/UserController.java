package com.example.server.controllers;

import com.example.server.engine.MainEngine;
import dtos.*;
import dtos.admin.DtoAllSimulationRequests;
import dtos.admin.DtoFinalSimulationsDetails;
import dtos.user.DtoRequestForAdmin;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
    private final MainEngine mainEngine;

    public UserController(MainEngine mainEngine) {
        this.mainEngine = mainEngine;
    }

    @GetMapping("/login")
    public String Login(@RequestParam String userName) {
       return this.mainEngine.getAllocationManager().addNameToSet(userName);
    }

    @GetMapping("/fetchRequests")
    public DtoAllSimulationRequests fetchRequestsForUser(@RequestParam("userName") String userName){
       return this.mainEngine.getAllocationManager().fetchRequestsForUser(userName);
    }

    @PutMapping("/sendRequestToAdmin")
    public boolean  submitRequestToAdmin (@RequestBody DtoRequestForAdmin dtoRequestForAdmin) {
        return this.mainEngine.getAllocationManager().addRequest(dtoRequestForAdmin);
    }

    @PutMapping("/executeSimulation")
    public String executeSimulation(@RequestParam("requestId") int requestId, @RequestBody DtoUiToEngine dtoSimulationDetails){
        this.mainEngine.executeSimulation(dtoSimulationDetails, requestId);
        return "simulation started";
    }

    @GetMapping("/previewWorlds")
    public List<DtoResponsePreview> getPreviewWorlds(){
        return mainEngine.previewWorldsInfo();
    }

    @PutMapping("/pauseSimulation")
    public String pauseSimulation(@RequestParam("simulationId") int simulationId, @RequestBody String requestBody){
        this.mainEngine.pauseCurrentSimulation(simulationId);
        return "simulation paused";
    }
    @PutMapping("/stopSimulation")
    public String stopSimulation(@RequestParam("simulationId") int simulationId, @RequestBody String requestBody){
        this.mainEngine.stopCurrentSimulation(simulationId);
        return "simulation stopped";
    }
    @PutMapping("/resumeSimulation")
    public String resumeSimulation(@RequestParam("simulationId") int simulationId, @RequestBody String requestBody){
        this.mainEngine.resumeCurretnSimulation(simulationId);
        return "simulation resumed";
    }
    @GetMapping("fetchAllProperties")
    public List<String> fetchAllProperties(@RequestParam("simulationId") int simulationId, @RequestParam("entityName") String entityName){
        return this.mainEngine.bringPropertiesByEntityName(simulationId, entityName);
    }
    @GetMapping("/fetchHistogramInfo")
    public DtoHistogramInfo fetchHistogramInfo(@RequestParam("simulationId") int simulationId, @RequestParam("entityName") String entityName, @RequestParam("propertyName") String propertyName){
        return this.mainEngine.fetchInfoOnChosenProperty(simulationId, entityName, propertyName);
    }
    @GetMapping("/fetchBarChartDetails")
    public List<DtoCountTickPopulation> fetchBarChartInfo(@RequestParam("simulationId") int simulationId){
        return this.mainEngine.getSimulationListOfPopulationPerTick(simulationId);
    }
    @GetMapping("/fetchSimulation")
    public DtoSimulationDetails fetchSimulationDetails(@RequestParam("simulationId") int simulationId){
        return this.mainEngine.getSimulationById(simulationId);
    }

    @GetMapping("/fetchSimulationsByUserName")
    public DtoAllSimulationDetails fetchAllSimulationDetail( @RequestParam("userName") String userName){
       return this.mainEngine.fetchAllSimulationByUserName(userName);
    }
}
