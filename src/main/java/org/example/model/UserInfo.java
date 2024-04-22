package org.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("UserInfo")
public class UserInfo {
  @Id
  private String id;
  private PermanentUserInfo permanentUserInfo;
  private TempUserInfo tempUserInfo;

  public UserInfo(PermanentUserInfo permanentUserInfo, TempUserInfo tempUserInfo) {
    this.permanentUserInfo = permanentUserInfo;
    this.tempUserInfo = tempUserInfo;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PermanentUserInfo getPermanentUserInfo() {
    return permanentUserInfo;
  }

  public void setPermanentUserInfo(PermanentUserInfo permanentUserInfo) {
    this.permanentUserInfo = permanentUserInfo;
  }

  public TempUserInfo getTempUserInfo() {
    if(tempUserInfo == null){
      return new TempUserInfo();
    }
    return tempUserInfo;
  }

  public void setTempUserInfo(TempUserInfo tempUserInfo) {
    this.tempUserInfo = tempUserInfo;
  }

  @Override
  public String toString() {
    return "UserInfo{" +
           "permanentUserInfo=" + permanentUserInfo +
           ", tempUserInfo=" + tempUserInfo +
           '}';
  }
}
