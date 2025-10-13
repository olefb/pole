package com.ole.pole.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record VoteEvent(
    Long pollId,
    Long optionId,
    Long voterId,
    LocalDateTime votedAt
) implements Serializable {}
