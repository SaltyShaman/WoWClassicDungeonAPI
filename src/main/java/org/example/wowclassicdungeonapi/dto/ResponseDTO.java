package org.example.wowclassicdungeonapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {

    private String answer;
    private List<Map<String, String>> messages;

    public MyResponse(String answer) {
        this.answer = answer;
    }
    public MyResponse(String answer, List<Map<String,String>> messages) {
        this.answer = answer;
        this.messages = messages;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public void setMessages(List<Map<String, String>> messages) {
        this.messages = messages;
    }
}
