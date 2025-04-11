package org.example.wowclassicdungeonapi.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.wowclassicdungeonapi.dto.ChatCompletionRequest;
import org.example.wowclassicdungeonapi.dto.ChatCompletionResponse;
import org.example.wowclassicdungeonapi.dto.MyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URI;

@Service
public class OpenAIService {

    public static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    @Value("${OPENAPIKEY}")
    private String OPENAPIKEY;

    @Value("${app.url}")
    public String URL;

    @Value("${app.model}")
    public String MODEL;

    @Value("${app.temperature}")
    public double TEMPERATURE;

    @Value("${app.max_tokens}")
    public int MAX_TOKENS;

    @Value("${app.frequency_penalty}")
    public double FREQUENCY_PENALTY;

    @Value("${app.presence_penalty}")
    public double PRESENCE_PENALTY;

    @Value("${app.top_p}")
    public double TOP_P;

    private WebClient client;

    public OpenAIService() {
        this.client = WebClient.create();
    }

    //Use this constructor for testing, to inject a mock client
    public OpenAIService(WebClient client) {
        this.client = client;
    }

    public MyResponse makeRequest(String userPrompt, String _systemMessage) {
//
        ChatCompletionRequest requestDto = new ChatCompletionRequest();
        requestDto.setModel(MODEL);
        requestDto.setTemperature(TEMPERATURE);
        requestDto.setMax_tokens(MAX_TOKENS);
        requestDto.setTop_p(TOP_P);
        requestDto.setFrequency_penalty(FREQUENCY_PENALTY);
        requestDto.setPresence_penalty(PRESENCE_PENALTY);
        requestDto.getMessages().add(new ChatCompletionRequest.Message("system", _systemMessage));
        requestDto.getMessages().add(new ChatCompletionRequest.Message("user", userPrompt));

        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        String err =  null;
        try {
            json = mapper.writeValueAsString(requestDto);
            System.out.println(json);
            ChatCompletionResponse response = client.post()
                    .uri(new URI(URL))
                    .header("Authorization", "Bearer " + OPENAPIKEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(json))
                    .retrieve()
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();
            String responseMsg = response.getChoices().get(0).getMessage().getContent();
            int tokensUsed = response.getUsage().getTotal_tokens();
            System.out.print("Tokens used: " + tokensUsed);
            System.out.print(". Cost ($0.0015 / 1K tokens) : $" + String.format("%6f",(tokensUsed * 0.0015 / 1000)));
            System.out.println(". For 1$, this is the amount of similar requests you can make: " + Math.round(1/(tokensUsed * 0.0015 / 1000)));
            return new MyResponse(responseMsg);
        }
        catch (WebClientResponseException e){
            //This is how you can get the status code and message reported back by the remote API
            logger.error("Error response status code: " + e.getRawStatusCode());
            logger.error("Error response body: " + e.getResponseBodyAsString());
            logger.error("WebClientResponseException", e);
            err = "Internal Server Error, due to a failed request to external service. You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
        catch (Exception e) {
            logger.error("Exception", e);
            err = "Internal Server Error - You could try again" +
                    "( While you develop, make sure to consult the detailed error message on your backend)";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, err);
        }
    }


    public String askGPT(String userPrompt) {
        ChatCompletionRequest requestDto = new ChatCompletionRequest();
        requestDto.setModel(MODEL);
        requestDto.setTemperature(TEMPERATURE);
        requestDto.setMax_tokens(MAX_TOKENS);
        requestDto.setTop_p(TOP_P);
        requestDto.setFrequency_penalty(FREQUENCY_PENALTY);
        requestDto.setPresence_penalty(PRESENCE_PENALTY);
        requestDto.getMessages().add(new ChatCompletionRequest.Message("system", "You are a helpful assistant."));
        requestDto.getMessages().add(new ChatCompletionRequest.Message("user", userPrompt));

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(requestDto);
            ChatCompletionResponse response = client.post()
                    .uri(new URI(URL))
                    .header("Authorization", "Bearer " + OPENAPIKEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(json))
                    .retrieve()
                    .bodyToMono(ChatCompletionResponse.class)
                    .block();

            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            } else {
                return "No response received from OpenAI.";
            }
        } catch (WebClientResponseException e) {
            logger.error("OpenAI API Error: {}", e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "OpenAI API error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Unexpected error when calling OpenAI", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error when calling OpenAI: " + e.getMessage());
        }
    }


}
