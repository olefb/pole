package com.ole.pole.controller;

import com.ole.pole.service.PollManager;
import com.ole.pole.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/votes")
public class VoteController {

    private PollManager pollManager;

    @Autowired
    public VoteController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping
    public Vote create(@RequestBody Vote vote) {
        vote.setVotedAt(LocalDateTime.now());
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