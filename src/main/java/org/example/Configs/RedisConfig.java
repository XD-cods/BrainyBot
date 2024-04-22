package org.example.Configs;

import org.example.model.UserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.Jedis;

@Configuration
@ComponentScan("org.example")
@EnableRedisRepositories("org.example.Repositories.Redis")
public class RedisConfig {
  @Bean
  public RedisConnectionFactory getConnection() {
    return new JedisConnectionFactory(new RedisStandaloneConfiguration("localhost"));
  }

  @Bean("redisTemplate")
  public RedisTemplate<Long, UserInfo> getRedisTemplate() {
    RedisTemplate<Long, UserInfo> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(getConnection());
    Jackson2JsonRedisSerializer<UserInfo> serializer = new Jackson2JsonRedisSerializer<>(UserInfo.class);
    redisTemplate.setDefaultSerializer(serializer);
    redisTemplate.setValueSerializer(serializer);
    return redisTemplate;
  }


}
