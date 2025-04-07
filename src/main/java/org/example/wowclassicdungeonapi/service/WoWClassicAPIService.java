package org.example.wowclassicdungeonapi.service;

import org.example.wowclassicdungeonapi.dto.CharacterDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class WoWClassicAPIService {

    @Value("${WOWAPIKEY}")
    private String WOWAPIKEY;

    private final WebClient client;

    public WoWClassicAPIService(WebClient.Builder webClientBuilder) {
        this.client = webClientBuilder.baseUrl("https://api.blizzard.com").build();
    }

    public CharacterDTO getCharacterInfo(String realm, String characterName) {
        String apiUrl = "/wow/character/{realm}/{character}?access_token=" + WOWAPIKEY;

        try {
            // Make API request and map response to CharacterDTO directly
            return client.get()
                    .uri(apiUrl, realm, characterName)  // Substitute realm and character dynamically
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> response.bodyToMono(String.class).doOnTerminate(() -> System.out.println("Error: " + response.statusCode())))
                    .bodyToMono(CharacterDTO.class)  // Automatically map JSON to CharacterDTO
                    .block();  // Block and wait for the result synchronously (in a real project, use reactive code if possible)
        } catch (WebClientResponseException ex) {
            // Handle error responses
            System.err.println("Error fetching WoW character info: " + ex.getResponseBodyAsString());
            return null;  // Return null or handle it as per your error management strategy
        }
    }
}
