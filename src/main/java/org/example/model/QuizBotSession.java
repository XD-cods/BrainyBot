package org.example.model;

import org.example.QuizBotSessionMode;

public class QuizBotSession {
  private QuizBotSessionMode botSessionMode;

  private QuizQuestions currentQuizQuestions;
  private UserQuizSession userQuizSession;
  private String currentTopicName;
  private int lastKeyboardBotMessageId = 0;
  private String lastKeyboardBotMessageText;
  private int countOfQuestion = 0;

  public QuizBotSession(QuizBotSessionMode botSessionMode) {
    this.botSessionMode = botSessionMode;
  }

  public String getCurrentTopicName() {
    return currentTopicName;
  }

  public void setCurrentTopicName(String currentTopicName) {
    this.currentTopicName = currentTopicName;
  }

  public UserQuizSession getUserQuizSession() {
    return userQuizSession;
  }

  public void setUserQuizSession(UserQuizSession userQuizSession) {
    this.userQuizSession = userQuizSession;
  }

  public int getLastKeyboardBotMessageId() {
    return lastKeyboardBotMessageId;
  }

  public void setLastKeyboardBotMessageId(int lastKeyboardBotMessageId) {
    this.lastKeyboardBotMessageId = lastKeyboardBotMessageId;
  }

  public String getLastKeyboardBotMessageText() {
    return lastKeyboardBotMessageText;
  }

  public void setLastKeyboardBotMessageText(String lastKeyboardBotMessageText) {
    this.lastKeyboardBotMessageText = lastKeyboardBotMessageText;
  }

  public QuizQuestions getCurrentQuiz() {
    return currentQuizQuestions;
  }

  public void setCurrentQuiz(QuizQuestions currentQuizQuestions) {
    this.currentQuizQuestions = currentQuizQuestions;
  }

  public int getCountOfQuestion() {
    return countOfQuestion;
  }

  public void setCountOfQuestion(int countOfQuestion) {
    this.countOfQuestion = countOfQuestion;
  }

  public QuizBotSessionMode getBotSessionMode() {
    return botSessionMode;
  }

  public void setBotSessionMode(QuizBotSessionMode botSessionMode) {
    this.botSessionMode = botSessionMode;
  }
}
