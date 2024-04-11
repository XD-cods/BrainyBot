package org.example.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestionTest {
  private Question question;

  @BeforeEach
  void setUp() {
    question = new Question();
  }

  @Test
  void getQuestion() {
    Assertions.assertNotNull(question.getQuestion());
  }

  @Test
  void getAnswerDescription() {
    Assertions.assertNotNull(question.getAnswerDescription());
  }

  @Test
  void getOptionList() {
    Assertions.assertNotNull(question.getOptionList());
  }
}