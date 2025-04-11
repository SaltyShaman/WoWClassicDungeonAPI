package org.example.wowclassicdungeonapi.controller;


import org.example.wowclassicdungeonapi.dto.MyResponse;
import org.example.wowclassicdungeonapi.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


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

    @PostMapping("/wowchat")
    public ResponseEntity<Map<String, String>> wowChat(@RequestBody Map<String, Object> body) {
        String question = (String) body.get("question");
        Map<String, Object> character = (Map<String, Object>) body.get("character");

        String prompt = "Her er en World of Warcraft karakter:\n" +
                "Navn: " + character.get("name") + "\n" +
                "Realm: " + ((Map<?, ?>) character.get("realm")).get("slug") + "\n" +
                "Level: " + character.get("level") + "\n" +
                "Race: " + ((Map<?, ?>) character.get("race")).get("name") + "\n" +
                "Class: " + ((Map<?, ?>) character.get("character_class")).get("name") + "\n\n" +
                "Spørgsmål: " + question;

        String answer = openAIService.askGPT(prompt); // Brug din eksisterende OpenAI-integration her
        return ResponseEntity.ok(Map.of("answer", answer));
    }


}
