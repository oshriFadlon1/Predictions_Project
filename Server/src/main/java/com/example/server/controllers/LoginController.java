package com.example.server.controllers;

import com.example.server.engine.MainEngine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final MainEngine mainEngine;

    public LoginController(MainEngine mainEngine) {
        this.mainEngine = mainEngine;
    }

    @GetMapping("/health")
    public String index(@RequestParam String userName) {
        return "Server is running";
    }
}
