package org.example.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "quiz")
public class Quiz {
  @Id
  @JsonIgnore
  private ObjectId id;
  private String topicName;
  private List<Question> questionList;

  public Quiz() {
  }

  public Quiz(String topicName, List<Question> questionList) {
    this.topicName = topicName;
    this.questionList = questionList;
  }

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  public List<Question> getQuestionList() {
    return questionList;
  }

  public void setQuestionList(List<Question> questionList) {
    this.questionList = questionList;
  }
  @JsonIgnore
  public ObjectId getId() {
    return id;
  }
  @JsonIgnore
  public void setId(ObjectId id) {
    this.id = id;
  }
}
