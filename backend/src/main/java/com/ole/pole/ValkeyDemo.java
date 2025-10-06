package com.ole.pole;

import io.valkey.UnifiedJedis;
import io.valkey.exceptions.JedisConnectionException;
import java.util.Map;

public class ValkeyDemo {

    private static final String VALKEY_URI = "redis://localhost:6379";
    private UnifiedJedis client;

    public ValkeyDemo() {
        this.client = new UnifiedJedis(VALKEY_URI);
    }

    public static void main(String[] args) {
        try {
            ValkeyDemo demo = new ValkeyDemo();
            demo.runStringAndExpireDemo();
            demo.runUseCase1_SetDemo();
            demo.runUseCase2_HashDemo();

        } catch (JedisConnectionException e) {
            System.err.println("Could not connect to Valkey/Redis at " + VALKEY_URI);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
        }
    }

    private void runStringAndExpireDemo() throws InterruptedException {
        client.set("user", "bob");
        System.out.println("SET user bob -> GET user: " + client.get("user"));

        client.set("user", "alice");
        System.out.println("SET user alice -> GET user: " + client.get("user"));

        // Cleanup existing keys
        client.del("user");
        client.set("user", "alice");

        // Expire
        client.expire("user", 5);
        System.out.println("EXPIRE user 5 -> TTL user: " + client.ttl("user") + " seconds remaining.");

        System.out.println("Waiting 5.5 seconds for key to expire...");
        Thread.sleep(5500);

        System.out.println("After wait -> TTL user: " + client.ttl("user"));
        System.out.println("After wait -> GET user: " + client.get("user"));
    }

    private void runUseCase1_SetDemo() {
        System.out.println("Use Case 1: Logged-in users (Set)");
        String key = "logged_in_users";
        client.del(key);

        // Initial state: no user is logged in
        System.out.println("Initial state: " + client.smembers(key));

        // User "alice" logs in
        client.sadd(key, "alice");
        System.out.println("Alice logs in: " + client.smembers(key));

        // User "bob" logs in
        client.sadd(key, "bob");
        System.out.println("Bob logs in: " + client.smembers(key));

        // Check if Alice is a member
        System.out.println("Is Alice a member? " + client.sismember(key, "alice")); // should be true

        // User "alice" logs off
        client.srem(key, "alice");
        System.out.println("Alice logs off: " + client.smembers(key));

        // User "eve" logs in
        client.sadd(key, "eve");
        System.out.println("Eve logs in (Final State): " + client.smembers(key));

        // Final check
        System.out.println("Is Alice a member? " + client.sismember(key, "alice")); // Should be false
    }

    private void runUseCase2_HashDemo() {
        System.out.println("Use Case 2: Poll Votes (Hash)");
        String pollId = "03ebcb7b-bd69-440b-924e-f5b7d664af7b";
        String key = "poll:votes:" + pollId;
        client.del(key); // Start from clean slate

        // Initial poll options and votes
        Map<String, String> initialVotes = Map.of(
                "option:Yes", "269",
                "option:No", "268",
                "option:IDoNotCare", "42"
        );

        // Set the initial state
        client.hmset(key, initialVotes);
        System.out.println("Initial State (HGETALL): " + client.hgetAll(key));

        // Increment the vote count for an option (option:Yes)
        client.hincrBy(key, "option:Yes", 1L);
        client.hincrBy(key, "option:Yes", 1L);

        // Increment the vote count for a different option (option:IDoNotCare)
        client.hincrBy(key, "option:IDoNotCare", 5L);

        // Get the updated count for 'option:Yes'
        String yesCount = client.hget(key, "option:Yes");
        System.out.println("HGET option:Yes after 2 votes: " + yesCount); // Should be 271

        // Get all votes to show the change
        System.out.println("Final State (HGETALL): " + client.hgetAll(key));
    }
}