package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.DAO.QuizDAO;
import org.example.model.Quiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DAORepository {
  QuizDAO quizDAO = new QuizDAO();
  private final Map<String, Quiz> quizesTopics = new HashMap<>();
  private static final Logger logger =  LogManager.getLogger(DAORepository.class);
  public DAORepository() {
    List<Quiz> quizzes = quizDAO.getAllQuiz();
    for(Quiz quiz : quizzes){
      quizesTopics.put(quiz.getTopicName(),quiz);
    }
  }

  public Quiz loadQuiz(String topicName) {
    logger.info("Was called loadQuiz in DAORepository");
    return quizesTopics.get(topicName);
  }

  public String[] getAllTopicNames() {
    logger.info("Was called getAllTopicNames in DAORepository");
    return quizDAO.getAllTopicName();
  }

  public int getCountOfQuiz(){
    return quizDAO.getCountOfQuiz();
  }

  public void addNewQuiz(Quiz quiz){
    quizDAO.addNewQuiz(quiz);
  }
}

