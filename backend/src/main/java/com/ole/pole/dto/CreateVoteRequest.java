package com.ole.pole.dto;

public class CreateVoteRequest {
    private String voterId;
    private Long optionId;
    private Long pollId;

    public CreateVoteRequest() {}

    public String getVoterId() { return voterId; }
    public void setVoterId(String voterId) { this.voterId = voterId; }

    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }

    public Long getPollId() { return pollId; }
    public void setPollId(Long pollId) { this.pollId = pollId; }
}
