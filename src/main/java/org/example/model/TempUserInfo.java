

package org.example.model;

import com.pengrad.telegrambot.model.Message;


public class TempUserInfo {
  private boolean choiceTopic = false;
  private boolean createMode = false;
  private UserQuizSession userQuizSession = null;
  private String currentTopicName;
  private Message lastkeyboardBotMessage = null;

  private boolean addQuizMode = false;
  private boolean updateChoiceTopic = false;
  private boolean updateInputFIle = false;

  public TempUserInfo() {
  }

  public boolean isUpdateChoiceTopic() {
    return updateChoiceTopic;
  }

  public void setUpdateChoiceTopic(boolean updateChoiceTopic) {
    this.updateChoiceTopic = updateChoiceTopic;
  }

  public boolean isUpdateInputFIle() {
    return updateInputFIle;
  }

  public void setUpdateInputFIle(boolean updateInputFIle) {
    this.updateInputFIle = updateInputFIle;
  }

  public boolean isAddQuizMode() {
    return addQuizMode;
  }

  public void setAddQuizMode(boolean addQuizMode) {
    this.addQuizMode = addQuizMode;
  }

  public String getCurrentTopicName() {
    return currentTopicName;
  }

  public void setCurrentTopicName(String currentTopicName) {
    this.currentTopicName = currentTopicName;
  }

  public boolean isTopicChosen() {
    return choiceTopic;
  }

  public void setChoiceTopic(boolean choiceTopic) {
    this.choiceTopic = choiceTopic;
  }

  public UserQuizSession getUserQuizSession() {
    return userQuizSession;
  }

  public void setUserQuizSession(UserQuizSession userQuizSession) {
    this.userQuizSession = userQuizSession;
  }

  public Message getLastkeyboardBotMessage() {
    return lastkeyboardBotMessage;
  }

  public void setLastkeyboardBotMessage(Message lastkeyboardBotMessage) {
    this.lastkeyboardBotMessage = lastkeyboardBotMessage;
  }

  public boolean isCreateMode() {
    return createMode;
  }

  public void setCreateMode(boolean createMode) {
    this.createMode = createMode;
  }
}
