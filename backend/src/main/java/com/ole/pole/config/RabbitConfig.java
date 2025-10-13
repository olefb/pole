package com.ole.pole.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String POLL_VOTE_EVENTS_EXCHANGE = "poll.vote.events.topic";
    public static final String POLL_APP_LISTENER_QUEUE = "poll-app.listener.queue";
    public static final String POLL_VOTE_BINDING_PATTERN = "poll.*.vote";

    /**
     * Converts Java objects to JSON format for sending and receiving messages.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Declares the Topic Exchange.
     */
    @Bean
    public TopicExchange pollVoteEventsExchange() {
        return new TopicExchange(POLL_VOTE_EVENTS_EXCHANGE, true, false);
    }

    /**
     * Declares the Queue to subscribe to all votes.
     */
    @Bean
    public Queue pollAppListenerQueue() {
        return new Queue(POLL_APP_LISTENER_QUEUE, true, false, false);
    }

    /**
     * Binds the queue to the exchange using the generic routing pattern.
     */
    @Bean
    public Binding bindingAllPollVotes(Queue pollAppListenerQueue, TopicExchange pollVoteEventsExchange) {
        return BindingBuilder.bind(pollAppListenerQueue)
                .to(pollVoteEventsExchange)
                .with(POLL_VOTE_BINDING_PATTERN);
    }
    
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}