package org.example.wowclassicdungeonapi.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.wowclassicdungeonapi.dto.CharacterDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
public class WoWClassicAPIService {

    private static final Logger logger = LoggerFactory.getLogger(WoWClassicAPIService.class);

    @Value("${blizzard.client-id}")
    private String clientId;

    @Value("${blizzard.client-secret}")
    private String clientSecret;

    @Value("${blizzard.token-url}")
    private String tokenUrl;

    @Value("${blizzard.api.base-url}")
    private String apiBaseUrl;

    private final WebClient webClient;

    private String cachedToken;
    private Instant tokenExpiryTime;

    public WoWClassicAPIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();  // We'll set baseUrl dynamically per request
    }

    // Method to get OAuth token from Blizzard
    public synchronized String getAccessToken() {
        // If token exists and hasn't expired, return it
        if (cachedToken != null && tokenExpiryTime != null && Instant.now().isBefore(tokenExpiryTime)) {
            return cachedToken;
        }

        // Otherwise, fetch a new token
        TokenResponse tokenResponse = webClient.post()
                .uri(tokenUrl)
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .bodyValue("grant_type=client_credentials")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        if (tokenResponse != null) {
            cachedToken = tokenResponse.getAccessToken();
            tokenExpiryTime = Instant.now().plusSeconds(tokenResponse.getExpiresIn() - 60); // renew 1 minute before expiry
            return cachedToken;
        } else {
            throw new RuntimeException("Failed to fetch access token");
        }
    }

    public CharacterDTO getCharacterInfo(String realm, String characterName) {
        String token = getAccessToken();
        try {
            // URL-encode the realm name and character name
            String encodedRealm = URLEncoder.encode(realm, StandardCharsets.UTF_8.toString());
            String encodedCharacterName = URLEncoder.encode(characterName, StandardCharsets.UTF_8.toString());

            // Log the request at this point
            logger.info("Fetching character info for realm: {} and character: {}", realm, characterName);

            // Build the API URL
            String apiUrl = apiBaseUrl + "/wow/character/" + encodedRealm + "/" + encodedCharacterName + "?access_token=" + token;

            // Make the API call and return the result
            return webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(CharacterDTO.class)
                    .block();
        } catch (Exception e) {
            // Log any error that occurs during the process
            logger.error("Error encoding URL or fetching character info: {}", e.getMessage());
            return null;
        }
    }

    // DTO for token response
    private static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private long expiresIn;

        public String getAccessToken() {
            return accessToken;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }
    }
}
