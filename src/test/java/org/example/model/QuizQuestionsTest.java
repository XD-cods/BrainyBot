package org.example.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizQuestionsTest {
  private QuizQuestions quizQuestions;

  @BeforeEach
  void setUp() {
    quizQuestions = new QuizQuestions();
  }

  @Test
  void getTopicName() {
    assertNotNull(quizQuestions.getTopicName());
  }

  @Test
  void setTopicName() {
    quizQuestions.setTopicName(null);
    Assertions.assertNotNull(quizQuestions.getTopicName());
    quizQuestions.setTopicName("java");
    Assertions.assertEquals(quizQuestions.getTopicName(),"java");
  }

  @Test
  void getQuestionList() {
    assertNotNull(quizQuestions.getQuestionList());
  }

  @Test
  void setQuestionList() {
    List<Question> questionList = new ArrayList<>();
    quizQuestions.setQuestionList(questionList);
    Assertions.assertEquals(quizQuestions.getQuestionList(),questionList);
  }
}