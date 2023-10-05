package com.example.server.controllers;

import com.example.server.engine.MainEngine;
import com.example.server.engineDtos.DtoResponseToController;
import dtos.*;
import dtos.admin.DtoAllSimulationRequests;
import dtos.admin.DtoFinalSimulationsDetails;
import dtos.admin.DtoSimulationsRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminController {
    private final MainEngine mainEngine;

    public AdminController(MainEngine mainEngine) {
        this.mainEngine = mainEngine;
    }
    @GetMapping("/login")
    public boolean adminLogin(){
        return mainEngine.validateIfAdminConnected();
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty())
        {
            DtoResponseToController response = mainEngine.addWorldDefinition(file.getInputStream());
            return response.getMessage();
        }

        return "Upload failed";
    }

    @GetMapping("/previewWorlds")
    public List<DtoResponsePreview> getPreviewWorlds(){
        return mainEngine.previewWorldsInfo();
    }

    @GetMapping("/getQueue")
    public DtoQueueManagerInfo getQueueInfo(){
        return mainEngine.getQueueManagerInfo();
    }

    @PatchMapping("/setThreadCount")
    public ResponseEntity<DtoResponseToController> setNewNumberOfThreads(@RequestBody String intValue){
        DtoResponseToController response = mainEngine.updateNumberOfThread(Integer.parseInt(intValue));
        return ResponseEntity.ok(response);
    }
    // to check if needt to convert from string to int
    @GetMapping("/fetchHistogramInfo")
    public DtoHistogramInfo fetchHistogramInfo(@RequestParam("simulationId") int simulationId, @RequestParam("entityName") String entityName, @RequestParam("propertyName") String propertyName){
        return mainEngine.fetchInfoOnChosenProperty(simulationId, entityName, propertyName);
    }
    // to check if needt to convert from string to int
    @GetMapping("/fetchFinalSimulationDetails")
    public DtoFinalSimulationsDetails fetchFinalSimulationDetails(@RequestParam("simulationId") int simulationId){
        return mainEngine.getFinalSimulationDetails(simulationId);
    }
    // to check if need to convert from string to int
    @GetMapping("/fetchAllProperties")
    public List<String> bringPropertiesByEntityName(@RequestParam("simulationId") int simulationId, @RequestParam("entityName") String entityName){
        return mainEngine.bringPropertiesByEntityName(simulationId, entityName);
    }
    // to check if needt to convert from string to int
    @GetMapping("/fetchBarChartDetails")
    public  List<DtoCountTickPopulation> getSimulationListOfPopulationPerTick(@RequestParam("simulationId") int simulationId){
        return mainEngine.getSimulationListOfPopulationPerTick(simulationId);
    }

    @GetMapping("/fetchAllEndedSimulations")
    public List<DtoFinalSimulationsDetails> fetchAllEndedSimulations(){
        return mainEngine.fetchAllEndedSimulations();
    }

    @GetMapping("/fetchAllRequests")
    public DtoAllSimulationRequests fetchAllRequests(){
        return this.mainEngine.getAllocationManager().fetchAllRequests();
    }

    @PutMapping("/rejectRequest")
    public String rejectRequest(@RequestBody DtoSimulationsRequest dtoSimulationsRequest){
        return this.mainEngine.getAllocationManager().rejectRequest(dtoSimulationsRequest);
    }

    @PutMapping("/approveRequest")
    public String approveRequest(@RequestBody DtoSimulationsRequest dtoSimulationsRequest){
        return this.mainEngine.getAllocationManager().approveRequest(dtoSimulationsRequest);
    }
}
