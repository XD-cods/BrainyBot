package org.example.model;

import java.util.Collection;
import java.util.Iterator;

public class UserQuizSession {

  private final int quizAmount;
  private final Iterator<Question> questionIterator;
  private Question currentQuestion;
  private int quizCounter = 0;
  private int rightAnswerCounter = 0;
  private boolean quizMode = true;

  public UserQuizSession(Collection<Question> questions) {
    this.questionIterator = questions.iterator();
    this.quizAmount = questions.size();
    this.currentQuestion = this.questionIterator.next();
  }

  public boolean isQuizMode() {
    return quizMode;
  }

  public boolean isNextQuestionAvailable() {
    return questionIterator.hasNext();
  }

  public void addRightCounter() {
    rightAnswerCounter++;
  }

  public void addQuizCounter() {
    quizCounter++;
  }

  public void setQuizMode(boolean quizMode) {
    this.quizMode = quizMode;
  }

  public Question getCurrentQuestion() {
    return currentQuestion;
  }

  public void getNextQuestion() {
    currentQuestion = questionIterator.next();
  }

  public int getQuizCounter() {
    return quizCounter;
  }

  public int getQuizAmount() {
    return quizAmount;
  }

  public int getRightAnswerCounter() {
    return rightAnswerCounter;
  }

  public int getWrongAnswerCounter(){
    return quizAmount - rightAnswerCounter;
  }
}
