package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "options")
public class QuestionOption {
  @Id
  @JsonIgnore
  private ObjectId id = new ObjectId();
  @JsonProperty("isAnswer")
  private boolean isAnswer = false;
  private String optionText = "";

  public QuestionOption() {

  }

  public QuestionOption(boolean isAnswer, String optionText) {
    this.isAnswer = isAnswer;
    this.optionText = optionText;
  }

  @JsonProperty("isAnswer")
  public boolean isAnswer() {
    return isAnswer;
  }

  @JsonProperty("isAnswer")
  public void setIsAnswer(boolean answer) {
    this.isAnswer = answer;
  }

  public String getOptionText() {
    return optionText;
  }

  public void setOptionText(String optionText) {
    this.optionText = optionText;
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
