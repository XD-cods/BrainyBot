package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "question")
public class Question {
  @Id
  @JsonIgnore
  private ObjectId id = new ObjectId();
  private String question = "";
  private String answerDescription = "";
  private List<QuestionOption> optionList = new ArrayList<>();

  public Question() {
  }

  public Question(String question, List<QuestionOption> questionOptions, String answerDescription) {
    this.question = question;
    this.answerDescription = answerDescription;
    this.optionList = questionOptions;
  }


  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getAnswerDescription() {
    return answerDescription;
  }

  public void setAnswerDescription(String answerDescription) {
    this.answerDescription = answerDescription;
  }

  public List<QuestionOption> getOptionList() {
    return optionList;
  }

  public void setOptionList(List<QuestionOption> optionList) {
    this.optionList = optionList;
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
