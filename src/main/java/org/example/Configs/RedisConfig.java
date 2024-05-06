package org.example.Configs;

import org.example.model.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
@ComponentScan("org.example")
@PropertySource("classpath:application.properties")
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {

  @Value("${redis.port:6379}")
  private int redisPort;
  @Value("${redis.hostName:localhost}")
  private String redisHostName;

  @Bean
  public JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHostName, redisPort);
    //todo connection pool
    return new JedisConnectionFactory(configuration);
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
    return container;
  }
}
