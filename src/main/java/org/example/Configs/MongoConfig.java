package org.example.Configs;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

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
  protected MongoClient createMongoClient(MongoClientSettings settings) {
    return MongoClients.create(connectionUri);
  }
}
