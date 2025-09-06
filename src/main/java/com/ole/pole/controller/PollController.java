package com.ole.pole.controller;

import com.ole.pole.service.PollManager;
import com.ole.pole.model.Poll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/polls")
public class PollController {

    private PollManager pollManager;

    @Autowired
    public PollController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping
    public Poll create(@RequestBody Poll poll) {
        poll.setPublishedAt(LocalDateTime.now());
        Long id = pollManager.createPoll(poll);
        poll.setId(id);
        return poll;
    }

    @GetMapping("/{id}")
    public Poll get(@PathVariable Long id) {
        return pollManager.getPoll(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody Poll poll) {
        poll.setId(id);
        pollManager.updatePoll(poll);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pollManager.deletePoll(id);
    }
}