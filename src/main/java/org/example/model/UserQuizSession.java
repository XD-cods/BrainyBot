package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash("UserQuizSession")
public class UserQuizSession {

  private int questionAmount;
  private List<Question> questionList;
  private Question currentQuestion;
  private int questionCounter = 0;
  private int rightAnswerCounter = 0;
  private boolean quizMode = true;

  public UserQuizSession() {
  }

  public UserQuizSession(Quiz quiz) {
    this.questionList = quiz.getQuestionList();
    this.questionAmount = questionList.size();
  }

  public void setQuestionAmount(int questionAmount) {
    this.questionAmount = questionAmount;
  }

  public void setCurrentQuestion(Question currentQuestion) {
    this.currentQuestion = currentQuestion;
  }

  public void setQuestionCounter(int questionCounter) {
    this.questionCounter = questionCounter;
  }

  public void setRightAnswerCounter(int rightAnswerCounter) {
    this.rightAnswerCounter = rightAnswerCounter;
  }

  public boolean isQuizMode() {
    return quizMode;
  }

  public void setQuizMode(boolean quizMode) {
    this.quizMode = quizMode;
  }

  public void addRightCounter() {
    if (rightAnswerCounter != questionAmount) {
      rightAnswerCounter++;
    }
  }

  public Question getCurrentQuestion() {
    return currentQuestion;
  }

  @JsonIgnore
  public Question getNextQuestion() {
    if(isNextQuestionAvailable()) {
      currentQuestion = questionList.get(questionCounter);
      questionCounter++;
      return currentQuestion;
    }
    return currentQuestion;
  }

  @JsonIgnore
  public boolean isNextQuestionAvailable() {
    return questionCounter < questionAmount;
  }

  public int getQuestionCounter() {
    return questionCounter;
  }

  public int getQuestionAmount() {
    return questionAmount;
  }

  public int getRightAnswerCounter() {
    return rightAnswerCounter;
  }

  public List<Question> getQuestionList() {
    return questionList;
  }

  public void setQuestionList(List<Question> questionList) {
    this.questionList = questionList;
  }
}