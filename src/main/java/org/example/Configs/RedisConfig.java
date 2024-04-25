package org.example.Configs;

import org.example.model.UserInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
@ComponentScan("org.example")
public class RedisConfig {

  @Bean
  public JedisConnectionFactory getConnection() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration("localhost", 6379);
    return new JedisConnectionFactory(configuration);
  }

  @Bean("redisTemplate")
  public RedisTemplate<Long, UserInfo> getRedisTemplate() {
    RedisTemplate<Long, UserInfo> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(getConnection());

    Jackson2JsonRedisSerializer<UserInfo> serializer = new Jackson2JsonRedisSerializer<>(UserInfo.class);
    redisTemplate.setDefaultSerializer(serializer);
    redisTemplate.setValueSerializer(serializer);
    redisTemplate.setKeySerializer(serializer);
    redisTemplate.setHashValueSerializer(serializer);
    return redisTemplate;
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer() {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(getConnection());
    KeyspaceNotificationListener keyspaceNotificationListener = new KeyspaceNotificationListener();
    container.addMessageListener(keyspaceNotificationListener, new PatternTopic("__keyevent@*__:expired"));
    return container;
  }


}
