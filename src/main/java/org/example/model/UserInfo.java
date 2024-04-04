package org.example.model;

public class UserInfo {
  private PermanentUserInfo permanentUserInfo;
  private TempUserInfo tempUserInfo;

  public UserInfo(PermanentUserInfo permanentUserInfo, TempUserInfo tempUserInfo) {
    this.permanentUserInfo = permanentUserInfo;
    this.tempUserInfo = tempUserInfo;
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
}
