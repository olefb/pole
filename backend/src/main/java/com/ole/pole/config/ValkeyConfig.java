package com.ole.pole.config;

import io.valkey.JedisPool;
import io.valkey.JedisPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValkeyConfig {

    @Value("${valkey.host}")
    private String host;

    @Value("${valkey.port}")
    private int port;

    @Bean
    public JedisPool valkeyJedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);

        poolConfig.setJmxEnabled(false);

        return new JedisPool(poolConfig, host, port);
    }
}