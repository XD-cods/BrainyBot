package org.example.configs;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import org.example.model.QuizQuestions;
import org.example.services.QuizService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.Subscription;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

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

  public MongoClient reconnectToDB() {
    MongoClient currentClient = mongoClient();
    currentClient.close();
    return MongoClients.create(new ConnectionString(connectionUri));
  }

  @Bean
  public DefaultMessageListenerContainer messageListenerContainer(MongoTemplate mongoTemplate, QuizService quizService) {
    DefaultMessageListenerContainer container = new DefaultMessageListenerContainer(mongoTemplate);
    container.start();
    ChangeStreamOptions changeStreamOptions = ChangeStreamOptions.builder()
            .filter(Aggregation.newAggregation(Aggregation.match(Criteria.where("operationType").is(OperationType.INSERT.getValue()))))
            .fullDocumentLookup(FullDocument.UPDATE_LOOKUP)
            .build();
    ChangeStreamRequest.ChangeStreamRequestOptions options = new ChangeStreamRequest.ChangeStreamRequestOptions(databaseName, "quizQuestions", changeStreamOptions);

    Subscription subscription = container.register(new ChangeStreamRequest<>(message -> {
      QuizQuestions quizQuestions = message.getBody();
      if (quizQuestions != null) {
        String topic = quizQuestions.getTopicName();
        quizService.addTopic(topic);
      }
    }, options), QuizQuestions.class);
    return container;
  }
}
