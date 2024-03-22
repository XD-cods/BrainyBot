package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.DAO.QuizDAO;
import org.example.model.Quiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizRepository {
  QuizDAO quizDAO = new QuizDAO();
  private final Map<String, Quiz> quizesTopics = new HashMap<>();
  private static final Logger logger =  LogManager.getLogger(QuizRepository.class);
  public QuizRepository() {
    List<Quiz> quizzes = quizDAO.getAllQuiz();
    for(Quiz quiz : quizzes){
      quizesTopics.put(quiz.getTopicName(),quiz);
    }
  }

  public Quiz loadQuiz(String topicName) {
    return quizesTopics.get(topicName);
  }

  public String[] getAllTopicNames() {
    return quizDAO.getAllTopicName();
  }
}

