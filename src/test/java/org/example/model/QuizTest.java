package org.example.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizTest {
  private Quiz quiz;

  @BeforeEach
  void setUp() {
    quiz = new Quiz();
  }

  @Test
  void getTopicName() {
    assertNotNull(quiz.getTopicName());
  }

  @Test
  void setTopicName() {
    quiz.setTopicName(null);
    Assertions.assertNotNull(quiz.getTopicName());
    quiz.setTopicName("java");
    Assertions.assertEquals(quiz.getTopicName(),"java");
  }

  @Test
  void getQuestionList() {
    assertNotNull(quiz.getQuestionList());
  }

  @Test
  void setQuestionList() {
    List<Question> questionList = new ArrayList<>();
    quiz.setQuestionList(questionList);
    Assertions.assertEquals(quiz.getQuestionList(),questionList);
  }
}