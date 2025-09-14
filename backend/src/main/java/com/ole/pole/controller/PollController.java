package com.ole.pole.controller;

import com.ole.pole.service.PollManager;
import com.ole.pole.model.Poll;
import com.ole.pole.model.User;
import com.ole.pole.model.VoteOption;
import com.ole.pole.dto.CreatePollRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/polls")
@CrossOrigin
public class PollController {

    private PollManager pollManager;

    @Autowired
    public PollController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping
    public Poll create(@RequestBody CreatePollRequest request) {
        // Create the poll
        Poll poll = new Poll();
        poll.setQuestion(request.getQuestion());
        poll.setPublishedAt(LocalDateTime.now());

        // Find the creator user
        Long creatorId = Long.parseLong(request.getUserId());
        User creator = pollManager.getUser(creatorId);
        if (creator == null) {
            throw new RuntimeException("User not found with id: " + creatorId);
        }
        poll.setCreator(creator);

        // Create the poll first to get an ID
        Long pollId = pollManager.createPoll(poll);
        poll.setId(pollId);

        // Create vote options
        List<VoteOption> voteOptions = new ArrayList<>();
        for (int i = 0; i < request.getOptions().size(); i++) {
            VoteOption voteOption = new VoteOption();
            voteOption.setCaption(request.getOptions().get(i));
            voteOption.setPresentationOrder(i);
            voteOption.setPoll(poll);

            Long optionId = pollManager.createVoteOption(voteOption);
            voteOption.setId(optionId);
            voteOptions.add(voteOption);
        }

        poll.setOptions(voteOptions);

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
