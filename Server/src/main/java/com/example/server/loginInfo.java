package com.example.server;

import controllers.MainEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class loginInfo {

    private final MainEngineService mainEngine;

    @Autowired
    public loginInfo(MainEngineService mainEngine) {
        this.mainEngine = mainEngine;
    }

    @GetMapping("/login")
    public String index(@RequestParam String userName) {
        System.out.println("Greetings from Spring Boot! to " + userName);
        return "Greetings from Spring Boot! to " + userName;
    }
}
