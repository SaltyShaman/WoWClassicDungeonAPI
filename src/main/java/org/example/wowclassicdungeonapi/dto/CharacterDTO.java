package org.example.wowclassicdungeonapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CharacterDTO {

    private String characterName;
    private String wowServer;
    private int characterLevel;

    public CharacterDTO(String characterName, String wowServer, int characterLevel) {
        this.characterName = characterName;
        this.wowServer = wowServer;
        this.characterLevel = characterLevel;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public String getWowServer() {
        return wowServer;
    }

    public void setWowServer(String wowServer) {
        this.wowServer = wowServer;
    }

    public int getCharacterLevel() {
        return characterLevel;
    }

    public void setCharacterLevel(int characterLevel) {
        this.characterLevel = characterLevel;
    }
}
