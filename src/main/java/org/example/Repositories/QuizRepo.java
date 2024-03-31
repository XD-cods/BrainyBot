package org.example.Repositories;

import org.example.model.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepo extends MongoRepository<Quiz, String> {
  @Query(value = "{}", fields = "{ 'topicName' : 1 }")
  public List<String> findAllTopicName();
}
