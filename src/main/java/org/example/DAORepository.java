package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Quiz;

import java.util.List;

public class DAORepository {
  private static final Logger logger = LogManager.getLogger(DAORepository.class);

  public DAORepository() {
  }

  public Quiz loadQuiz(String topicName) {
    logger.info("Was called loadQuiz in DAORepository");
    return new Quiz();
  }

  public List<String> getAllTopicNames() {
    logger.info("Was called getAllTopicNames in DAORepository");
    return List.of();
  }

  public int getCountOfQuiz() {
    return 0;
  }


}

