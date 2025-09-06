package com.ole.pole.controller;

import com.ole.pole.service.PollManager;
import com.ole.pole.model.Poll;
import com.ole.pole.model.VoteOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vote-options")
public class VoteOptionController {

    private PollManager pollManager;

    @Autowired
    public VoteOptionController(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    @PostMapping("/poll/{pollId}")
    public VoteOption create(@PathVariable Long pollId, @RequestBody VoteOption voteOption) {
        Poll poll = pollManager.getPoll(pollId);
        if (poll != null) {
            voteOption.setPoll(poll);
            poll.getOptions().add(voteOption);
            Long id = pollManager.createVoteOption(voteOption);
            voteOption.setId(id);
        }
        return voteOption;
    }

    @GetMapping("/poll/{pollId}")
    public List<VoteOption> getByPoll(@PathVariable Long pollId) {
        List<VoteOption> all = pollManager.listVoteOptions();
        return all.stream()
                .filter(vo -> vo.getPoll() != null && vo.getPoll().getId().equals(pollId))
                .collect(Collectors.toList());
    }

    @PutMapping("/{optionId}")
    public void update(@PathVariable Long optionId, @RequestBody VoteOption voteOption) {
        VoteOption existing = pollManager.getVoteOption(optionId);
        if (existing != null) {
            voteOption.setId(optionId);
            voteOption.setPoll(existing.getPoll());
            pollManager.updateVoteOption(voteOption);
        }
    }
}