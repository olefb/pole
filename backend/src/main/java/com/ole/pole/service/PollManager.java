package com.ole.pole.service;

import com.ole.pole.dto.VoteEvent;
import com.ole.pole.model.User;
import com.ole.pole.model.Poll;
import com.ole.pole.model.VoteOption;
import com.ole.pole.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.Map;

@Component
public class PollManager {

    private HashMap<Long, User> users;
    private HashMap<Long, Poll> polls;
    private HashMap<Long, VoteOption> voteOptions;
    private HashMap<Long, Vote> votes;

    private final AtomicLong userId = new AtomicLong(1);
    private final AtomicLong pollId = new AtomicLong(1);
    private final AtomicLong optionId = new AtomicLong(1);
    private final AtomicLong voteId = new AtomicLong(1);

    private final ValkeyVoteService valkeyVoteService;

    private final PollEventPublisher pollEventPublisher;

    @Autowired
    public PollManager(ValkeyVoteService valkeyVoteService, PollEventPublisher pollEventPublisher) {
        this.users = new HashMap<>();
        this.polls = new HashMap<>();
        this.voteOptions = new HashMap<>();
        this.votes = new HashMap<>();
        this.valkeyVoteService = valkeyVoteService;
        this.pollEventPublisher = pollEventPublisher;
    }

    // Users
    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUser(Long id) {
        return users.get(id);
    }

    public Long createUser(User user) {
        Long id = userId.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        return id;
    }

    public void updateUser(User user) {
        if (user.getId() != null) {
            users.put(user.getId(), user);
        }
    }

    // Polls
    public List<Poll> listPolls() {
        return new ArrayList<>(polls.values());
    }

    public Poll getPoll(Long id) {
        return polls.get(id);
    }

    public Long createPoll(Poll poll) {
        Long id = pollId.getAndIncrement();
        poll.setId(id);
        polls.put(id, poll);
        pollEventPublisher.registerPollTopic(id);

        if (poll.getCreator() != null) {
            poll.getCreator().getPolls().add(poll);
        }
        return id;
    }

    public void updatePoll(Poll poll) {
        if (poll.getId() != null) {
            polls.put(poll.getId(), poll);
        }
    }

    public void deletePoll(Long id) {
        polls.remove(id);
    }

    // VoteOptions
    public List<VoteOption> listVoteOptions() {
        return new ArrayList<>(voteOptions.values());
    }

    public VoteOption getVoteOption(Long id) {
        return voteOptions.get(id);
    }

    public Long createVoteOption(VoteOption voteOption) {
        Long id = optionId.getAndIncrement();
        voteOption.setId(id);
        voteOptions.put(id, voteOption);

        if (voteOption.getPoll() != null) {
            voteOption.getPoll().getOptions().add(voteOption);
        }
        return id;
    }

    public void updateVoteOption(VoteOption voteOption) {
        if (voteOption.getId() != null) {
            voteOptions.put(voteOption.getId(), voteOption);
        }
    }

    // Votes
    public List<Vote> listVotes() {
        return new ArrayList<>(votes.values());
    }

    public Vote getVote(Long id) {
        return votes.get(id);
    }

    public Long createVote(Vote vote) {
        // Build the event from the Vote object (which was constructed in the controller)
        VoteEvent event = new VoteEvent(
                vote.getPoll().getId(),
                vote.getOption().getId(),
                vote.getVoter().getId(),
                vote.getVotedAt()
        );

        // Publish the event to RabbitMQ
        pollEventPublisher.publishVoteEvent(event);

        // placeholder return value; the actual persistence happens asynchronously in recordVoteFromEvent()
        return -1L;
    }

    public Long recordVoteFromEvent(VoteEvent event) {

        User voter = getUser(event.voterId());
        Poll poll = getPoll(event.pollId());
        VoteOption option = getVoteOption(event.optionId());

        if (voter == null || poll == null || option == null) {
            System.err.println("Event data invalid: User, Poll, or Option not found.");
            return -1L;
        }

        // create the Vote entity
        Vote vote = new Vote();
        vote.setVotedAt(event.votedAt());
        vote.setVoter(voter);
        vote.setOption(option);
        vote.setPoll(poll);

        // persist the Vote and update the cache
        Long id = voteId.getAndIncrement();
        vote.setId(id);
        votes.put(id, vote);

        voter.getVotes().add(vote);
        option.getVotes().add(vote);
        poll.getVotes().add(vote);

        valkeyVoteService.incrementVote(vote.getPoll().getId(), vote.getOption().getId());

        System.out.println("Vote recorded from event: " + id);
        return id;
    }

    /**
     * Implements the Cache-Aside pattern to get aggregated vote counts.
     */
    public Map<Long, Long> getPollResults(Long pollId) {

        Map<String, String> cachedCounts = valkeyVoteService.getVoteCounts(pollId);

        if (cachedCounts != null && !cachedCounts.isEmpty()) {
            return cachedCounts.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> Long.valueOf(e.getKey()), // optionId
                            e -> Long.valueOf(e.getValue()) // count
                    ));
        }

        Poll poll = getPoll(pollId);
        if (poll == null) {
            return Map.of();
        }

        Map<Long, Long> dbAggregatedCounts = poll.getVotes().stream()
                .collect(Collectors.groupingBy(
                        vote -> vote.getOption().getId(),
                        Collectors.counting()
                ));

        // Ensure all options are represented, even those with 0 votes
        Map<Long, Long> finalResults = poll.getOptions().stream()
                .collect(Collectors.toMap(
                        VoteOption::getId,
                        option -> dbAggregatedCounts.getOrDefault(option.getId(), 0L)
                ));

        Map<String, String> cacheMap = finalResults.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue())
                ));

        valkeyVoteService.populateCache(pollId, cacheMap);

        return finalResults;
    }

    public void updateVote(Vote vote) {
        if (vote.getId() != null) {
            votes.put(vote.getId(), vote);
        }
    }

    public void deleteVote(Long id) {
        votes.remove(id);
    }
}