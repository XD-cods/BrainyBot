package org.example.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    Assertions.assertNotNull(userQuizSession.getNextQuestion());
  }


  @Test
  void incRightCounter() {
    Assertions.assertEquals(userQuizSession.getRightAnswerCounter(),0);
    userQuizSession.incRightCounter();
    Assertions.assertEquals(userQuizSession.getRightAnswerCounter(),1);
  }

  @Test
  void getCurrentQuestion() {

  }

  @Test
  void getNextQuestion() {
    Assertions.assertEquals(userQuizSession.getNextQuestion(), question1);
    Assertions.assertEquals(userQuizSession.getNextQuestion(), question2);
  }

  @Test
  void getQuestionCounter() {
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

  }
}