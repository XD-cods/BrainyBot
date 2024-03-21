package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.DAO.QuizDAO;
import org.example.model.Question;

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
  private QuizDAO quizDAO = new QuizDAO();
  private final Map<String, Path> quizesTopics = new HashMap<>();
  private static final Logger logger =  LogManager.getLogger(QuizRepository.class);
  public QuizRepository(String jsonPath) {
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Path.of(jsonPath))) {
      for (Path path : directoryStream) {
        String fileName = path.getFileName().toString();
        String topicName = fileName.substring(0, fileName.indexOf(".json"));
        quizesTopics.put(topicName, path);
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to read quizes", e);
    }
  }

  public Collection<Question> loadQuestions(String topicName) {
    if (!quizesTopics.containsKey(topicName)) {
      return List.of();
    }
    try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(quizesTopics.get(topicName)))) {
      Gson gson = new GsonBuilder().create();
      return Arrays.asList(gson.fromJson(reader, Question[].class));
    } catch (IOException e) {
      logger.error("Error appeared while loading quiz");
      return List.of();
    }
  }

  public String[] getAllTopicNames() {
    return quizesTopics.keySet().toArray(new String[]{});
  }

}