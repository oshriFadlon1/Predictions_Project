package com.example.server.controllers;

import com.example.server.engine.MainEngine;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final MainEngine mainEngine;

    public AdminController(MainEngine mainEngine) {
        this.mainEngine = mainEngine;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty())
        {
            mainEngine.addWorldDefinition(file.getInputStream());
        }

        return "Upload failed";
    }
}
