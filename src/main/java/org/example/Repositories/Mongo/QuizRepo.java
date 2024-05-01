package org.example.Repositories.Mongo;

import org.example.model.Question;
import org.example.model.Quiz;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepo extends MongoRepository<Quiz, String> {
  @Query(value = "{}", fields = "{ 'topicName' : 1, '_id' : 0  }")
  List<String> findAllTopicName();

  @Query("{'topicName' : ?0}")
  Quiz findByTopicName(String topicName);

  @DeleteQuery("{'topicName' : ?0}")
  void deleteByTopicName(String topicName);

  @Query("{'topicName' : ?0}")
  @Update("{$push : {'questionList' : {$each : ?1}}}")
  void addByTopic(String topicName, List<Question> questionList);
}
