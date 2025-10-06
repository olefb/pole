package com.ole.pole.dto;

public class PollResultOptionDTO {
    private Long optionId;
    private String caption;
    private Long voteCount;

    public PollResultOptionDTO() {
        this.optionId = optionId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public String getCaption() {
        return caption;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public void setVoteCount(Long orDefault) {
        this.voteCount = orDefault;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}