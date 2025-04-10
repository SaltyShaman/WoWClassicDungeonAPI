package org.example.wowclassicdungeonapi.controller;


import org.example.wowclassicdungeonapi.dto.MyResponse;
import org.example.wowclassicdungeonapi.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatGPTResponseController {

    private final OpenAIService openAIService;

    final static String SYSTEM_MESSAGE = "You are a helpful assistant that only provides answers."+
            " The user should be asking a simple question";

    @Autowired
    public ChatGPTResponseController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping("/v1/joke")
    public MyResponse getAnswer(@RequestParam String about) {
        return openAIService.makeRequest(about, SYSTEM_MESSAGE);
    }



}
