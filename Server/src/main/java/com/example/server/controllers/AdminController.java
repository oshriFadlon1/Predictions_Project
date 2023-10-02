package com.example.server.controllers;

import com.example.server.engine.MainEngine;
import com.example.server.engineDtos.DtoResponseToController;
import dtos.DtoHistogramInfo;
import dtos.DtoQueueManagerInfo;
import dtos.DtoResponsePreview;
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

    @GetMapping("/fetchHistogramInfo")
    public DtoHistogramInfo fetchHistogramInfo(@RequestParam("simulationId") int simulationId, @RequestParam("entityName") String entityName, @RequestParam("propertyName") String propertyName){
        return mainEngine.fetchInfoOnChosenProperty(simulationId, entityName, propertyName);
    }
}
