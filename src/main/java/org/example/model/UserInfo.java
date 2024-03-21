

package org.example.model;

import com.pengrad.telegrambot.model.Message;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.NotFound;

@Entity
@Table(name = "user_info")
public class UserInfo {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @Column(name = "user_id")
  private Long userId;
  @Column(name = "user_name")
  private String userName;
  @NotFound
  private boolean choiceQuiz = false;
  @NotFound
  private UserQuizSession userQuizSession = null;
  @NotFound
  private String currentTopicName;
  @NotFound
  private Message lastBotMessage = null;

  public UserInfo() {
  }

  public UserInfo(int id, Long userId, String userName) {
    this.id = id;
    this.userId = userId;
    this.userName = userName;
  }

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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
