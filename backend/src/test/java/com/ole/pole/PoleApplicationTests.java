package com.ole.pole;

import com.ole.pole.model.Poll;
import com.ole.pole.model.User;
import com.ole.pole.model.Vote;
import com.ole.pole.model.VoteOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PoleApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCompleteVotingScenario() {
        // Step 1: Create two users
        User user1 = new User();
        user1.setUsername("Richard");
        user1.setEmail("richard@example.com");

        ResponseEntity<User> createUser1Response = restTemplate.postForEntity("/users", user1, User.class);
        assertEquals(200, createUser1Response.getStatusCodeValue());
        User createdUser1 = createUser1Response.getBody();
        assertNotNull(createdUser1.getId());

        User user2 = new User();
        user2.setUsername("Joaquin");
        user2.setEmail("joaquin@example.com");

        ResponseEntity<User> createUser2Response = restTemplate.postForEntity("/users", user2, User.class);
        assertEquals(200, createUser2Response.getStatusCodeValue());
        User createdUser2 = createUser2Response.getBody();
        assertNotNull(createdUser2.getId());

        // Step 2: Create poll by user1
        Poll poll = new Poll();
        poll.setQuestion("What's your favorite food?");
        poll.setDeadline(LocalDateTime.now().plusDays(1));
        poll.setCreator(createdUser1);

        ResponseEntity<Poll> createPollResponse = restTemplate.postForEntity("/polls", poll, Poll.class);
        assertEquals(200, createPollResponse.getStatusCodeValue());
        Poll createdPoll = createPollResponse.getBody();
        assertNotNull(createdPoll.getId());
        assertEquals(poll.getQuestion(), createdPoll.getQuestion());

        // Step 3: Add two vote options to the poll
        VoteOption option1 = new VoteOption();
        option1.setCaption("Tofu");
        option1.setPresentationOrder(1);

        ResponseEntity<VoteOption> createOption1Response = restTemplate.postForEntity("/vote-options/poll/" + createdPoll.getId(), option1, VoteOption.class);
        assertEquals(200, createOption1Response.getStatusCodeValue());
        VoteOption createdOption1 = createOption1Response.getBody();
        assertNotNull(createdOption1.getId());
        assertEquals(option1.getCaption(), createdOption1.getCaption());

        VoteOption option2 = new VoteOption();
        option2.setCaption("Lentils");
        option2.setPresentationOrder(2);

        ResponseEntity<VoteOption> createOption2Response = restTemplate.postForEntity("/vote-options/poll/" + createdPoll.getId(), option2, VoteOption.class);
        assertEquals(200, createOption2Response.getStatusCodeValue());
        VoteOption createdOption2 = createOption2Response.getBody();
        assertNotNull(createdOption2.getId());
        assertEquals(option2.getCaption(), createdOption2.getCaption());

        // Step 4: List vote options for the poll
        ResponseEntity<VoteOption[]> listOptionsResponse = restTemplate.getForEntity("/vote-options/poll/" + createdPoll.getId(), VoteOption[].class);
        assertEquals(200, listOptionsResponse.getStatusCodeValue());
        VoteOption[] options = listOptionsResponse.getBody();
        assertNotNull(options);
        assertEquals(2, options.length);

        // Step 5: Create vote by user2 for option1
        Vote vote = new Vote();
        vote.setVoter(createdUser2);
        vote.setOption(createdOption1);
        vote.setPoll(createdPoll);

        ResponseEntity<Vote> createVoteResponse = restTemplate.postForEntity("/votes", vote, Vote.class);
        assertEquals(200, createVoteResponse.getStatusCodeValue());
        Vote createdVote = createVoteResponse.getBody();
        assertNotNull(createdVote.getId());

        // Step 6: List votes to verify
        ResponseEntity<Vote[]> listVotesResponse = restTemplate.getForEntity("/votes", Vote[].class);
        assertEquals(200, listVotesResponse.getStatusCodeValue());
        Vote[] votesAfterFirstVote = listVotesResponse.getBody();
        assertNotNull(votesAfterFirstVote);
        assertEquals(1, votesAfterFirstVote.length);
        assertEquals(createdVote.getId(), votesAfterFirstVote[0].getId());

        // Step 7: Change vote to option2 (delete current vote and create new one)
        restTemplate.delete("/votes/" + createdVote.getId());

        // Verify vote is deleted
        ResponseEntity<Vote[]> listVotesAfterDeleteResponse = restTemplate.getForEntity("/votes", Vote[].class);
        assertEquals(200, listVotesAfterDeleteResponse.getStatusCodeValue());
        Vote[] votesAfterDelete = listVotesAfterDeleteResponse.getBody();
        assertNotNull(votesAfterDelete);
        assertEquals(0, votesAfterDelete.length);

        // Create new vote for option2
        Vote newVote = new Vote();
        newVote.setVoter(createdUser2);
        newVote.setOption(createdOption2);
        newVote.setPoll(createdPoll);

        ResponseEntity<Vote> createNewVoteResponse = restTemplate.postForEntity("/votes", newVote, Vote.class);
        assertEquals(200, createNewVoteResponse.getStatusCodeValue());
        Vote createdNewVote = createNewVoteResponse.getBody();
        assertNotNull(createdNewVote.getId());

        // Step 8: List votes again to verify new vote
        ResponseEntity<Vote[]> finalListVotesResponse = restTemplate.getForEntity("/votes", Vote[].class);
        assertEquals(200, finalListVotesResponse.getStatusCodeValue());
        Vote[] finalVotes = finalListVotesResponse.getBody();
        assertNotNull(finalVotes);
        assertEquals(1, finalVotes.length);
        assertEquals(createdNewVote.getId(), finalVotes[0].getId());

        // Step 9: Delete the remaining vote
        restTemplate.delete("/votes/" + createdNewVote.getId());

        // Step 10: Delete the poll
        restTemplate.delete("/polls/" + createdPoll.getId());

        // Step 10: Verify that votes are gone
        ResponseEntity<Vote[]> finalListVotesAfterDeleteResponse = restTemplate.getForEntity("/votes", Vote[].class);
        assertEquals(200, finalListVotesAfterDeleteResponse.getStatusCodeValue());
        Vote[] votesAfterPollDelete = finalListVotesAfterDeleteResponse.getBody();
        assertNotNull(votesAfterPollDelete);
        assertEquals(0, votesAfterPollDelete.length);

        // Try to get the poll, should return null body
        ResponseEntity<Poll> getPollResponse = restTemplate.getForEntity("/polls/" + createdPoll.getId(), Poll.class);
        assertNull(getPollResponse.getBody());
    }
}