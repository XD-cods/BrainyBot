package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Question;
import org.example.model.QuizInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizRepository {
  private static final Logger logger =  LogManager.getLogger(QuizRepository.class);
  public QuizRepository() {
  }

  public Collection<Question> loadQuestions(String topicName) {
    QuizInfo quizInfo = QuizInfoDAO.getQuizInfo(topicName);
    if(quizInfo == null){
      logger.error("Error appeared while loading quiz");
      return List.of();
    }
    String json = quizInfo.getQuizData();
    Gson gson = new GsonBuilder().create();
    return Arrays.asList(gson.fromJson(quizInfo.getQuizData(), Question[].class));

  }

  public String[] getAllTopicNames() {
    return QuizInfoDAO.getTopicsName().toArray(new String[0]);
  }

}
