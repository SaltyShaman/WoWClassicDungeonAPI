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

    private final WoWClassicAPIService wowClassicAPIService;;

    @Autowired
    public WoWClassicController(WoWClassicAPIService wowClassicAPIService) {
        this.wowClassicAPIService = wowClassicAPIService;
    }

    @GetMapping("/wow-classic/character")
    public ResponseEntity<CharacterDTO> getCharacterInfo(@RequestParam String realm, @RequestParam String characterName) {
        CharacterDTO character = wowClassicAPIService.getCharacterInfo(realm, characterName);
        return ResponseEntity.ok(character);
    }

}
