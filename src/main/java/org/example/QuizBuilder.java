package org.example;

import org.example.model.Question;

import java.util.ArrayList;
import java.util.List;

public class QuizBuilder {
  private String topicName = "";
  private List<Question> questionList = new ArrayList<>();
  private boolean inputOptions = false;
  private boolean inputAnswerOptions = false;
  private boolean inputAnswerDescription = false;

  public QuizBuilder(String topicName) {
    this.topicName = topicName;
  }

  public QuizBuilder() {
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

  public boolean isInputOptions() {
    return inputOptions;
  }

  public void setInputOptions(boolean inputOptions) {
    this.inputOptions = inputOptions;
  }

  public boolean isInputAnswerOptions() {
    return inputAnswerOptions;
  }

  public void setInputAnswerOptions(boolean inputAnswerOptions) {
    this.inputAnswerOptions = inputAnswerOptions;
  }

  public boolean isInputAnswerDescription() {
    return inputAnswerDescription;
  }

  public void setInputAnswerDescription(boolean inputAnswerDescription) {
    this.inputAnswerDescription = inputAnswerDescription;
  }
}
