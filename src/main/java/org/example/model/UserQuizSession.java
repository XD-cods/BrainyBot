package org.example.model;

import java.util.List;

public class UserQuizSession {

  private final List<Question> questions;
  private int currentQuestionIndex = 0;
  private int questionCounter = 0;
  private int rightAnswerCounter = 0;

  public UserQuizSession(List<Question> questions) {
    this.questions = questions;
  }

  public List<Question> getQuestions() {
    return questions;
  }

  public int getRightAnswerCounter() {
    return rightAnswerCounter;
  }

  public int getQuestionCounter() {
    return questionCounter;
  }

  public void incQuestionCounter() {
    questionCounter++;
  }

  public int getCurrentQuestionIndex() {
    return currentQuestionIndex;
  }

  public void incCurrentQuestionIndex() {
    currentQuestionIndex++;
  }

  public void incRightCounter() {
    rightAnswerCounter++;
  }
}