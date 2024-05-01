package org.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("TempUserInfo")
public class TempUserInfo {
  @Id
  private String id;
  private boolean choiceTopic = false;
  private boolean choiceCountOfQuestion = false;
  private Quiz currentQuiz;
  private UserQuizSession userQuizSession;
  private String currentTopicName;
  private int lastKeyboardBotMessageId;
  private String lastKeyboardBotMessageText;
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

  public boolean isChoiceTopic() {
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
