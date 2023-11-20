package org.example.model;

public class UserInfo {
  private boolean choiceQuiz = false;
  private UserQuizSession userQuizSession = null;
  private int quizIndex = 0;

  public int getQuizIndex() {
    return quizIndex;
  }

  public void setQuizIndex(int quizIndex) {
    this.quizIndex = quizIndex;
  }

  public boolean isChoiceQuiz() {
    return choiceQuiz;
  }

  public void setChoiceQuiz(boolean choiceQuiz) {
    this.choiceQuiz = choiceQuiz;
  }

  public UserQuizSession getUserQuizSession() {
    return userQuizSession;
  }

  public void setUserQuizSession(UserQuizSession userQuizSession) {
    this.userQuizSession = userQuizSession;
  }
}
