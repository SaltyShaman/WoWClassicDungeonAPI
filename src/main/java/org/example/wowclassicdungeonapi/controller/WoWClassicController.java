package org.example.wowclassicdungeonapi.controller;

import org.example.wowclassicdungeonapi.dto.CharacterDTO;
import org.example.wowclassicdungeonapi.service.WoWClassicAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WoWClassicController {

    private final WoWClassicAPIService wowClassicAPIService;

    @Autowired
    public WoWClassicController(WoWClassicAPIService wowClassicAPIService) {
        this.wowClassicAPIService = wowClassicAPIService;
    }

    @GetMapping("/wow-classic/character")
    public ResponseEntity<?> getCharacterInfo(
            @RequestParam String realm,
            @RequestParam String characterName) {

        if (realm == null || realm.isEmpty() || characterName == null || characterName.isEmpty()) {
            return ResponseEntity.badRequest().body("Realm and character name must be provided.");
        }

        CharacterDTO character = wowClassicAPIService.getCharacterInfo(realm, characterName);

        System.out.println("Received request for realm: " + realm + " and character: " + characterName);

        if (character == null) {
            return ResponseEntity.status(404).body("Character not found or API error occurred.");
        }
        return ResponseEntity.ok(character);
    }
}
