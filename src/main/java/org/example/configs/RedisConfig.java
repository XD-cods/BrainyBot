package org.example.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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

@Deprecated(forRemoval = true)
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
  public JedisPoolConfig jedisPoolConfig() {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(8);
    poolConfig.setMaxIdle(8);
    poolConfig.setMinIdle(2);
    return poolConfig;
  }

  @Bean
  public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHostName, redisPort);
    JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
            .usePooling()
            .poolConfig(jedisPoolConfig)
            .build();
    // edit log level on error
    return new JedisConnectionFactory(configuration, clientConfig);
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
  public RedisMessageListenerContainer redisMessageListenerContainer(
          KeyspaceNotificationListener keyspaceNotificationListener, JedisConnectionFactory jedisConnectionFactory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(jedisConnectionFactory);
    container.addMessageListener(keyspaceNotificationListener, new PatternTopic("__keyevent@*__:expired"));
    return container;
  }
}
