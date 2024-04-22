package org.example.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Repositories.Mongo.QuizRepo;
import org.example.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {
  String pathTopicsName = "src/main/resources/topics";

  private final QuizRepo quizRepo;

  @Autowired
  public QuizService(QuizRepo quizRepo) {
    this.quizRepo = quizRepo;
  }

  public List<String> findAllTopicName() {
    ObjectMapper objectMapper = new ObjectMapper();
    List<String> topics = quizRepo.findAllTopicName();
    if (topics == null) {
      return List.of();
    }
    return topics.stream().map(topic -> {
      try {
        JsonNode jsonNode = objectMapper.readTree(topic);
        return jsonNode.get("topicName").asText();
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toList());
  }

  public void deleteAllQuiz() {
    quizRepo.deleteAll();
    try {
      Files.delete(Path.of(pathTopicsName));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void updateTopicsFile() {
    long countOfQuiz = quizRepo.count();
    if (countOfQuiz <= 0) {
      return;
    }
    List<String> allTopicName = this.findAllTopicName();

    try {
      Files.write(Paths.get(pathTopicsName), allTopicName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<String> readTopicsFromFile() {
    Path path = Path.of(pathTopicsName);
    long countOfQuiz = quizRepo.count();
    File file = new File(path.toString());
    if (countOfQuiz == 0 && file.exists()) {
      file.delete();
    }

    if (file.exists()) {
      try {
        return Files.readAllLines(path);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return List.of();
  }

  public void insertNewQuiz(Quiz quiz) {
    if (quizRepo.findByTopicName(quiz.getTopicName()) != null) {
      quizRepo.addByTopic(quiz.getTopicName(), quiz.getQuestionList());
      return;
    }
    quizRepo.save(quiz);
    try (FileWriter outputStream = new FileWriter(pathTopicsName)) {
      outputStream.write(String.valueOf(findAllTopicName()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    updateTopicsFile();
  }

  public Quiz findByTopicName(String topicName) {
    return quizRepo.findByTopicName(topicName);
  }

  public void updateQuizByTopicName(String topicName, Quiz newQuiz) {
    quizRepo.deleteByTopicName(topicName);
    insertNewQuiz(newQuiz);
  }


}
