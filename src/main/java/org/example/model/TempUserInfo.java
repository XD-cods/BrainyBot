

package org.example.model;

import com.pengrad.telegrambot.model.Message;


public class TempUserInfo {
  private boolean choiceTopic = false;
  private boolean createMode = false;
  private boolean choiceCountOfQuestion = false;
  private UserQuizSession userQuizSession = null;
  private Quiz currentQuiz;
  private String currentTopicName;
  private Message lastkeyboardBotMessage = null;
  private int countOfQuestion = 0;

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

  public boolean isChoiceCountOfQuestion() {
    return choiceCountOfQuestion;
  }

  public void setChoiceCountOfQuestion(boolean choiceCountOfQuestion) {
    this.choiceCountOfQuestion = choiceCountOfQuestion;
  }

  public Quiz getCurrentQuiz() {
    return currentQuiz;
  }

  public void setCurrentQuiz(Quiz currentQuiz) {
    this.currentQuiz = currentQuiz;
  }

  public int getCountOfQuestion() {
    return countOfQuestion;
  }

  public void setCountOfQuestion(int countOfQuestion) {
    this.countOfQuestion = countOfQuestion;
  }
}
