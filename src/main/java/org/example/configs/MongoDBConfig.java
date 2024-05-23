package org.example.configs;


import com.mongodb.ConnectionString;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.services.QuizService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "org.example")
@PropertySource("classpath:application.properties")
@EnableMongoRepositories("org.example.repositories")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

  @Value("${spring.data.mongodb.database}")
  private String databaseName;

  @Value("${spring.data.mongodb.uri}")
  private String connectionUri;


  @NotNull
  @Override
  protected String getDatabaseName() {
    return databaseName;
  }

  @Bean
  @NotNull
  @Override
  public MongoClient mongoClient() {
    return MongoClients.create(new ConnectionString(connectionUri));
  }

  @Bean
  public ChangeStreamIterable<Document> runEventListener(MongoTemplate mongoTemplate, QuizService quizService) {
    MongoCollection<Document> collection = mongoTemplate.getCollection("quizQuestions");
    List<Bson> pipeline = List.of(
            Aggregates.match(Filters.in("operationType", Arrays.asList("insert", "delete"))));
    ChangeStreamIterable<Document> changeStream = collection.watch(pipeline);
    changeStream.forEach(event -> {
      String operationType = event.getOperationTypeString();
      if (operationType == null) {
        return;
      }

      switch (operationType) {
        case "insert" -> {
          Document document = event.getFullDocument();
          if (document != null && document.containsKey("topicName")) {
            quizService.addTopic(document.getString("topicName"));
          }
        }
        case "delete" -> quizService.updateTopics();
      }

    });
    return changeStream;
  }
}
