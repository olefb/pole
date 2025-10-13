package com.ole.pole.service;

import com.ole.pole.dto.VoteEvent;
import com.ole.pole.model.Poll;
import com.ole.pole.model.User;
import com.ole.pole.model.VoteOption;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PollsDataInitializer {

    private final PollManager pollManager;

    // Inject PollManager
    public PollsDataInitializer(PollManager pollManager) {
        this.pollManager = pollManager;
    }

    public void initializeData() {
        populateDataDirectly();
    }

    private void populateDataDirectly() {

        User alice = new User("alice", "alice@online.com");
        pollManager.createUser(alice); // ID 1
        User bob = new User("bob", "bob@bob.home");
        pollManager.createUser(bob); // ID 2
        User eve = new User("eve", "eve@mail.org");
        pollManager.createUser(eve); // ID 3

        Poll poll1 = new Poll();
        poll1.setQuestion("Vim or Emacs?");
        poll1.setPublishedAt(LocalDateTime.now());
        poll1.setCreator(alice);
        pollManager.createPoll(poll1); // ID 1

        VoteOption vim = new VoteOption();
        vim.setCaption("Vim");
        vim.setPresentationOrder(0);
        vim.setPoll(poll1);
        pollManager.createVoteOption(vim); // ID 1

        VoteOption emacs = new VoteOption();
        emacs.setCaption("Emacs");
        emacs.setPresentationOrder(1);
        emacs.setPoll(poll1);
        pollManager.createVoteOption(emacs); // ID 2

        Poll poll2 = new Poll();
        poll2.setQuestion("Pineapple on Pizza");
        poll2.setPublishedAt(LocalDateTime.now());
        poll2.setCreator(eve);
        pollManager.createPoll(poll2); // ID 2

        VoteOption yes = new VoteOption();
        yes.setCaption("Yes! Yammy!");
        yes.setPresentationOrder(0);
        yes.setPoll(poll2);
        pollManager.createVoteOption(yes); // ID 3

        VoteOption no = new VoteOption();
        no.setCaption("Mamma mia: Nooooo!");
        no.setPresentationOrder(1);
        no.setPoll(poll2);
        pollManager.createVoteOption(no); // ID 4

        // Alice votes for Vim (Poll 1, Option 1)
        pollManager.recordVoteFromEvent(new VoteEvent(
                poll1.getId(), vim.getId(), alice.getId(), LocalDateTime.now().minusSeconds(5)
        ));

        // Bob votes for Vim (Poll 1, Option 1)
        pollManager.recordVoteFromEvent(new VoteEvent(
                poll1.getId(), vim.getId(), bob.getId(), LocalDateTime.now().minusSeconds(4)
        ));

        // Eve votes for Emacs (Poll 1, Option 2)
        pollManager.recordVoteFromEvent(new VoteEvent(
                poll1.getId(), emacs.getId(), eve.getId(), LocalDateTime.now().minusSeconds(3)
        ));

        // Eve votes for Yes (Poll 2, Option 3)
        pollManager.recordVoteFromEvent(new VoteEvent(
                poll2.getId(), yes.getId(), eve.getId(), LocalDateTime.now().minusSeconds(2)
        ));
    }
}