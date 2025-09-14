package com.ole.pole.controller;

import com.ole.pole.dto.CreateVoteRequest;
import com.ole.pole.model.Poll;
import com.ole.pole.model.User;
import com.ole.pole.model.VoteOption;
import com.ole.pole.service.PollManager;
import com.ole.pole.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/votes")
@CrossOrigin
public class VoteController {

    private PollManager pollManager;

    @Autowired
    public VoteController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping
    public Vote create(@RequestBody CreateVoteRequest request) {
        Vote vote = new Vote();
        vote.setVotedAt(LocalDateTime.now());

        // Set the voter
        Long voterId = Long.parseLong(request.getVoterId());
        User voter = pollManager.getUser(voterId);
        if (voter == null) {
            throw new RuntimeException("Voter not found with id: " + voterId);
        }
        vote.setVoter(voter);

        // Set the option
        VoteOption option = pollManager.getVoteOption(request.getOptionId());
        if (option == null) {
            throw new RuntimeException("Vote option not found with id: " + request.getOptionId());
        }
        vote.setOption(option);

        // Set the poll
        Poll poll = pollManager.getPoll(request.getPollId());
        if (poll == null) {
            throw new RuntimeException("Poll not found with id: " + request.getPollId());
        }
        vote.setPoll(poll);

        Long id = pollManager.createVote(vote);
        vote.setId(id);
        return vote;
    }

    @GetMapping
    public List<Vote> list() {
        return pollManager.listVotes();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Vote vote = pollManager.getVote(id);
        if (vote != null) {
            pollManager.deleteVote(id);
        }
    }
}