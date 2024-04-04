package org.example.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PermanentUserInfo {
  @Id
  private ObjectId id;
  private String userName;
  private Long userId;
  private boolean isAdmin = false;

  public PermanentUserInfo() {
  }

  public PermanentUserInfo(String userName, boolean isAdmin) {
    this.userName = userName;
    this.isAdmin = isAdmin;
  }

  public PermanentUserInfo(String userName, Long userId, boolean isAdmin) {
    this.userName = userName;
    this.userId = userId;
    this.isAdmin = isAdmin;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public boolean isAdmin() {
    return isAdmin;
  }

  public void setAdmin(boolean admin) {
    isAdmin = admin;
  }
}
