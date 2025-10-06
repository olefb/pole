package com.ole.pole.service;

import io.valkey.Jedis;
import io.valkey.JedisPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ValkeyVoteService {

    private final JedisPool jedisPool;
    private static final String POLL_VOTES_KEY_PREFIX = "poll:votes:";
    private static final long CACHE_TTL_SECONDS = TimeUnit.HOURS.toSeconds(1);

    @Autowired
    public ValkeyVoteService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * The core logic for incrementing the vote counter in Valkey.
     */
    public void incrementVote(Long pollId, Long optionId) {
        String key = POLL_VOTES_KEY_PREFIX + pollId;
        String field = String.valueOf(optionId);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hincrBy(key, field, 1);
            jedis.expire(key, CACHE_TTL_SECONDS);
        }
    }

    /**
     * Retrieves all vote counts for a poll from the Valkey cache.
     */
    public Map<String, String> getVoteCounts(Long pollId) {
        String key = POLL_VOTES_KEY_PREFIX + pollId;

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        }
    }

    /**
     * Populates the cache with aggregated data from the database/primary source.
     */
    public void populateCache(Long pollId, Map<String, String> counts) {
        String key = POLL_VOTES_KEY_PREFIX + pollId;

        if (!counts.isEmpty()) {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.hmset(key, counts);
                jedis.expire(key, CACHE_TTL_SECONDS);
            }
        }
    }
}