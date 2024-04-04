package org.example.Configs;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoClientSettingsFactoryBean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan(basePackages = "org.example")
@PropertySource("application.properties")
@EnableMongoRepositories("org.example.Repositories")
public class MongoConfig extends AbstractMongoClientConfiguration {
  @Value("${spring.data.mongodb.databaseName}")
  private String dataBaseName;
  @Value("${spring.data.mongodb.uri}")
  private String connectionUri;

  @Override
  protected String getDatabaseName() {
    return dataBaseName;
  }

  @Override
  @Bean
  public MongoClient mongoClient() {
    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionUri))
            .applyToClusterSettings(builder ->
                    builder.serverSelectionTimeout(2000, TimeUnit.MILLISECONDS)) // Таймаут выбора сервера
            .retryWrites(true) // Автоматическая повторная попытка записи
            .build();
    return MongoClients.create(settings);
  }

  @Bean
  public MongoClientSettings mongoClientSettings() {
    return MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionUri))
            .applyToClusterSettings(builder ->
                    builder.serverSelectionTimeout(2000, TimeUnit.MILLISECONDS)) // Таймаут выбора сервера
            .retryWrites(true) // Автоматическая повторная попытка записи
            .build();
  }

  @Bean
  public MongoClient mongoClientWithRetry(MongoClientSettings mongoClientSettings) {
    return MongoClients.create(mongoClientSettings);
  }
}