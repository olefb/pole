package com.ole.pole.listener;

import com.ole.pole.config.RabbitConfig;
import com.ole.pole.dto.VoteEvent;
import com.ole.pole.service.PollManager;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PollEventListener {

    private final PollManager pollManager;

    public PollEventListener(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    /**
     * Listens on the dedicated queue for VoteEvent messages.
     */
    @RabbitListener(queues = RabbitConfig.POLL_APP_LISTENER_QUEUE)
    public void handleVoteEvent(VoteEvent event) {
        System.out.println("\n[EVENT LISTENER] Received Vote Event: " + event);

        Long newVoteId = pollManager.recordVoteFromEvent(event);

        System.out.println("[EVENT LISTENER] Vote successfully persisted with ID: " + newVoteId + "\n");
    }
}