package org.example.Configs;

import org.example.model.UserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
@ComponentScan("org.example")
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {

  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("localhost", 6379);
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxIdle(5);
    poolConfig.setMaxTotal(5);
    poolConfig.setMinIdle(0);
    poolConfig.setBlockWhenExhausted(true);
    poolConfig.setMaxWait(Duration.ofSeconds(1));
    poolConfig.setTestWhileIdle(true);
    poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(1));

    JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().connectTimeout(Duration.ofSeconds(1000)).usePooling().poolConfig(poolConfig).build();

    return new JedisConnectionFactory(configuration, jedisClientConfiguration);
  }

  @Bean("redisTemplate")
  public RedisTemplate<Long, UserInfo> getRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
    RedisTemplate<Long, UserInfo> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory);
    Jackson2JsonRedisSerializer<UserInfo> serializer = new Jackson2JsonRedisSerializer<>(UserInfo.class);
    redisTemplate.setDefaultSerializer(serializer);
    redisTemplate.setValueSerializer(serializer);
    redisTemplate.setKeySerializer(serializer);
    redisTemplate.setHashValueSerializer(serializer);
    return redisTemplate;
  }

  @Bean
  public KeyspaceNotificationListener keyspaceNotificationListener() {
    return new KeyspaceNotificationListener();
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(KeyspaceNotificationListener keyspaceNotificationListener, JedisConnectionFactory jedisConnectionFactory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(jedisConnectionFactory);
    container.addMessageListener(keyspaceNotificationListener, new PatternTopic("__keyevent@*__:expired"));
    System.out.println("container started");
    return container;
  }
}
