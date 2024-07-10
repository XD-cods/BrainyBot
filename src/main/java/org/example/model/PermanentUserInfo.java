package org.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class PermanentUserInfo {
  @Id
  @Field(name = "userId")
  private Long userId = 0L;
  @Field(name = "userName")
  private String userName = "";

  public PermanentUserInfo() {
  }

  public PermanentUserInfo(String userName, Long userId) {
    this.userName = userName;
    this.userId = userId;
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

}
