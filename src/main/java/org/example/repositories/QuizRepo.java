package org.example.repositories;

import org.example.model.QuizQuestions;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepo extends MongoRepository<QuizQuestions, String> {

  @Query("{'topicName' : ?0}")
  QuizQuestions findByTopicName(String topicName);

  @Aggregation({"{ $match: { topicName: ?0 } }",
          "{ $project: {topicName: 1,questionList: 1,_class: 1} }",
          "{ $unwind: \"$questionList\" }",
          "{ $sample: { size: ?1 } }",
          "{ $group: {_id: \"$_id\", topicName:{$first:\"$topicName\"},questionList: { $push: \"$questionList\" },_class:{$first:\"$_class\"}} }",
  })
  QuizQuestions findRandomQuestionsByTopicName(String topicName, int n);

  @Aggregation({
          "{$project: {_id: 0,topicName: 1}}",
          "{$group: {_id: 0,topicName: { $push: \"$topicName\" }}}",
          "{$project: {topicName: 1,_id: 0}}"
  })
  String findAllTopic();
}
