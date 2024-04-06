package org.example.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class PermanentUserInfo {
  @Id
  private ObjectId id;
  @Field(name = "userName")
  private String userName;
  @Field(name = "userId")
  private Long userId;
  @Field(name = "isAdmin")
  private boolean isAdmin = false;

  public PermanentUserInfo() {
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

  public boolean getIsAdmin() {
    return isAdmin;
  }

  public void setAdmin(boolean admin) {
    isAdmin = admin;
  }
}
