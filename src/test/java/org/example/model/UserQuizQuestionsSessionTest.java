package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class UserQuizQuestionsSessionTest {
  private UserQuizSession userQuizSession;
  private Question question1;
  private Question question2;

  @BeforeEach
  public void setUp() {
  List<Question> questionList = new ArrayList<>();
  question1 = new Question("asddsasda",new ArrayList<>(),"asdsdasd");
  question2 = new Question("asddsasdsssa",new ArrayList<>(),"asdsdasd");
  questionList.add(question1);
  questionList.add(question2);
  QuizQuestions quizQuestions = new QuizQuestions();
  quizQuestions.setQuestionList(questionList);
  userQuizSession = new UserQuizSession(quizQuestions);
  }

  @Test
  void isNextQuestionAvailable(){
    Assertions.assertTrue(userQuizSession.isNextQuestionAvailable());
  }

  @Test
  void isNextQuestionNotAvailable() {
    userQuizSession = new UserQuizSession(new QuizQuestions());
    Assertions.assertFalse(userQuizSession.isNextQuestionAvailable());
  }

  @Test
  void incRightCounter() {
    Assertions.assertEquals(userQuizSession.getRightAnswerCounter(),0);
    userQuizSession.incRightCounter();
    Assertions.assertEquals(userQuizSession.getRightAnswerCounter(),1);
  }

  @Test
  void getCurrentQuestion() {
    Assertions.assertNull(userQuizSession.getCurrentQuestionIndex());
    userQuizSession.getNextQuestion();
    Assertions.assertEquals(userQuizSession.getCurrentQuestionIndex(), question1);
    userQuizSession.getNextQuestion();
    Assertions.assertEquals(userQuizSession.getCurrentQuestionIndex(), question2);
    userQuizSession.getNextQuestion();
    Assertions.assertEquals(userQuizSession.getCurrentQuestionIndex(), question2);
  }

  @Test
  void getNextQuestion() {
    Assertions.assertEquals(userQuizSession.getNextQuestion(), question1);
    Assertions.assertEquals(userQuizSession.getNextQuestion(), question2);
  }

  @Test
  void getQuestionCounter() {
    Assertions.assertEquals(userQuizSession.getQuestionCounter(),0);
    userQuizSession.getNextQuestion();
    Assertions.assertEquals(userQuizSession.getQuestionCounter(),1);
    userQuizSession.getNextQuestion();
    Assertions.assertEquals(userQuizSession.getQuestionCounter(),2);
    userQuizSession.getNextQuestion();
    Assertions.assertEquals(userQuizSession.getQuestionCounter(),2);
  }

  @Test
  void getQuestionAmount() {
    Assertions.assertEquals(userQuizSession.getQuestionAmount(),2);
  }

  @Test
  void getRightAnswerCounter() {
    Assertions.assertEquals(userQuizSession.getRightAnswerCounter(),0);
    userQuizSession.incRightCounter();
    Assertions.assertEquals(userQuizSession.getRightAnswerCounter(),1);
    userQuizSession.incRightCounter();
    Assertions.assertEquals(userQuizSession.getRightAnswerCounter(),2);
    userQuizSession.incRightCounter();
    Assertions.assertEquals(userQuizSession.getRightAnswerCounter(),2);
  }
}