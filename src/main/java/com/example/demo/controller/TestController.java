package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.KubernetesService;

import ch.qos.logback.core.model.Model;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {
    private final KubernetesService kubernetesService;
    public TestController(KubernetesService kubernetesService) {
        this.kubernetesService = kubernetesService;
    }
    @RequestMapping(value = "/runCurl", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<List<String>> runCurl(@RequestBody Map<String, String> params, Model model){
        String apiServer = params.get("apiServer");
        String token = params.get("token");
        System.out.println(apiServer+"here is the end");
        System.out.println(token+"here is the end");  
        List<String> logMessage = kubernetesService.deployJobFromYaml(apiServer, token);
        return ResponseEntity.ok(logMessage);
    }
}