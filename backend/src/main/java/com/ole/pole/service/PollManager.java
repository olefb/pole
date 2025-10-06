package com.ole.pole.service;

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

    @Autowired
    public PollManager(ValkeyVoteService valkeyVoteService) {
        this.users = new HashMap<>();
        this.polls = new HashMap<>();
        this.voteOptions = new HashMap<>();
        this.votes = new HashMap<>();
        this.valkeyVoteService = valkeyVoteService;
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
        Long id = voteId.getAndIncrement();
        vote.setId(id);
        votes.put(id, vote);
        valkeyVoteService.incrementVote(vote.getPoll().getId(), vote.getOption().getId());
        return id;
    }

    /**
     * Implements the Cache-Aside pattern to get aggregated vote counts.
     */
    public Map<Long, Long> getPollResults(Long pollId) {

        // 1. Try to fetch results from Valkey cache
        Map<String, String> cachedCounts = valkeyVoteService.getVoteCounts(pollId);

        if (cachedCounts != null && !cachedCounts.isEmpty()) {
            // 2. If 'yes', return results at once
            return cachedCounts.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> Long.valueOf(e.getKey()), // optionId
                            e -> Long.valueOf(e.getValue()) // count
                    ));
        }

        // 3. Otherwise, client has to contact the database (via JPA) and aggregate
        //    (NOTE: In a real app, this would be a single JPA query. Here, we simulate aggregation
        //     on our in-memory data structures, which is the 'expensive' operation).
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

        // 4. Before returning, put the current state into the cache
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