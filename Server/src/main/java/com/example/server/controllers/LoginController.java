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

    @GetMapping("/login")
    public String index(@RequestParam String userName) {
        System.out.println("Greetings from Spring Boot! to " + userName);
        return "Greetings from Spring Boot! to " + userName;
    }
}
