package org.example.model;

import com.pengrad.telegrambot.model.Message;

public class UserInfo {
  private boolean choiceQuiz = false;
  private UserQuizSession userQuizSession = null;
  private String currentTopicName;
  private Message lastBotMessage = null;

  public String getCurrentTopicName() {
    return currentTopicName;
  }

  public void setCurrentTopicName(String currentTopicName) {
    this.currentTopicName = currentTopicName;
  }

  public boolean isTopicChosen() {
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

  public Message getLastBotMessage() {
    return lastBotMessage;
  }

  public void setLastBotMessage(Message lastBotMessage) {
    this.lastBotMessage = lastBotMessage;
  }
}
