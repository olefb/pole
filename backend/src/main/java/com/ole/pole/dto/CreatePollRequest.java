package com.ole.pole.dto;

import java.util.List;

public class CreatePollRequest {
    private String userId;
    private String question;
    private List<String> options;

    public CreatePollRequest() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}