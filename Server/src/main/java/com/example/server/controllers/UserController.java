package com.example.server.controllers;

import com.example.server.engine.MainEngine;
import dtos.DtoResponsePreview;
import dtos.DtoUiToEngine;
import dtos.admin.DtoAllSimulationRequests;
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

}
