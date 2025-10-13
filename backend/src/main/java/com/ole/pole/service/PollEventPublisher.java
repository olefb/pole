package com.ole.pole.service;

import com.ole.pole.config.RabbitConfig;
import com.ole.pole.dto.VoteEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PollEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public PollEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void registerPollTopic(Long pollId) {
        System.out.println("Poll Topic Registered for ID " + pollId + " (Routing Key: poll." + pollId + ".vote)");
    }

    public void publishVoteEvent(VoteEvent event) {
        String routingKey = "poll." + event.pollId() + ".vote";

        System.out.println("Publishing Vote Event to Exchange: " + RabbitConfig.POLL_VOTE_EVENTS_EXCHANGE +
                " with Routing Key: " + routingKey);

        rabbitTemplate.convertAndSend(RabbitConfig.POLL_VOTE_EVENTS_EXCHANGE, routingKey, event);
    }
}