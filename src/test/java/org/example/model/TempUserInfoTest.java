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
  void getCurrentTopicName() {
    Assertions.assertNotNull(tempUserInfo.getCurrentTopicName());
  }

  @Test
  void getUserQuizSession() {
    Assertions.assertNotNull(tempUserInfo.getUserQuizSession());
  }

  @Test
  void getLastkeyboardBotMessage() {
    Assertions.assertNotNull(tempUserInfo.getLastkeyboardBotMessage());
  }

  @Test
  void getCurrentQuiz() {
    Assertions.assertNotNull(tempUserInfo.getCurrentQuiz());
  }

  @Test
  void getCountOfQuestion() {
    Assertions.assertEquals(tempUserInfo.getCountOfQuestion(), 0);
  }
}