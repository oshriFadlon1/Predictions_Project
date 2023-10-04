package com.example.server.controllers;

import com.example.server.engine.MainEngine;
import dtos.admin.DtoAllSimulationRequests;
import dtos.user.DtoRequestForAdmin;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {
    private final MainEngine mainEngine;

    public UserController(MainEngine mainEngine) {
        this.mainEngine = mainEngine;
    }

    @GetMapping("/login")
    public Boolean Login(@RequestParam String userName) {

    }

    @GetMapping("/fetchRequests")
    public DtoAllSimulationRequests fetchRequestsForUser(@RequestParam("userName") String userName){

    }

    @PutMapping("/sendRequestToAdmin")
    public boolean  submitRequestToAdmin (@RequestBody DtoRequestForAdmin dtoRequestForAdmin) {

    }

}
