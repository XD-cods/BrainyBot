package org.example.Repositories;

import org.example.model.Question;
import org.example.model.Quiz;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface QuizRepo extends MongoRepository<Quiz, String> {
  @Query(value = "{}", fields = "{ 'topicName' : 1, '_id' : 0  }")
  public List<String> findAllTopicName();

  @Query("{'topicName' : ?0}")
  public Quiz findByTopicName(String topicName);

  @DeleteQuery("{'topicName' : ?0}")
  public void deleteByTopicName(String topicName);

  @Query("{'topicName' : ?0}")
  @Update("{$push : {'questionList' : {$each : ?1}}}")
  public void addByTopic(String topicName, List<Question> questionList);
}
