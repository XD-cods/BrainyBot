package org.example.Configs;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan(basePackages = "org.example")
@PropertySource("application.properties")
@EnableMongoRepositories("org.example.Repositories.Mongo")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

//  private String dataBaseName = System.getenv("DATABASE_NAME");
//  private String connectionUri = System.getenv("MONGO_URL");

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
    return MongoClients.create(new ConnectionString(connectionUri));
  }

  public MongoClient reconnectToDB() {
    MongoClient currentClient = mongoClient();
    currentClient.close();
    return MongoClients.create(new ConnectionString(connectionUri));
  }
}
