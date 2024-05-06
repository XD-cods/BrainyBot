package org.example.Configs;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan(basePackages = "org.example")
@PropertySource("classpath:application.properties")
@EnableMongoRepositories("org.example.Repositories.Mongo")
public class MongoDBConfig extends AbstractMongoClientConfiguration {


  @Value("${spring.data.mongodb.database}")
  private String dataBaseName;
  @Value("${spring.data.mongodb.uri}")
  private String connectionUri;

  @NotNull
  @Override
  protected String getDatabaseName() {
    return dataBaseName;
  }

  @Bean
  @NotNull
  @Override
  public MongoClient mongoClient() {
    return MongoClients.create(new ConnectionString(connectionUri));
  }

  public MongoClient reconnectToDB() {
    MongoClient currentClient = mongoClient();
    currentClient.close();
    return MongoClients.create(new ConnectionString(connectionUri));
  }
}
