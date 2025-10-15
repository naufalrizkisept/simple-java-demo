package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfig {
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.password}")
    private String redisPassword;
    @Value("${redis.port}")
    private int redisPort;
    @Value("${redis.database}")
    private int redisDatabase;

    @Bean
    public RedisConnectionFactory redisConnection() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                redisHost,
                redisPort
        );
        config.setPassword(redisPassword);
        config.setDatabase(redisDatabase);
        log.info("Connected to Redis Factory: {}:{}", redisHost, redisPort);
        log.info("Redis DB index: {}", redisDatabase);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnection());
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
}
