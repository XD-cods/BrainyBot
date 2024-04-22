package org.example.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TempUserInfoTest {
  private TempUserInfo tempUserInfo;

  @BeforeEach
  void setUp() {
    tempUserInfo = new TempUserInfo();
  }

  @Test
  void getCountOfQuestion() {
    Assertions.assertEquals(tempUserInfo.getCountOfQuestion(), 0);
  }
}