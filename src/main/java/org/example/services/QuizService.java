package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.repositories.QuizRepo;
import org.example.model.QuizQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {
  private final QuizRepo quizRepo;
  String pathTopicsName = "topics"; //todo fetch topic list on start

  @Autowired
  public QuizService(QuizRepo quizRepo) {
    this.quizRepo = quizRepo;
  }

  public List<String> findAllTopicName() {
    //todo fetch topic list on start
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

  @Deprecated(forRemoval = true)
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

  @Deprecated(forRemoval = true)
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

  public void insertNewQuiz(QuizQuestions quizQuestions) {
    if (quizRepo.findByTopicName(quizQuestions.getTopicName()) != null) {
      quizRepo.addByTopic(quizQuestions.getTopicName(), quizQuestions.getQuestionList());
      return;
    }
    quizRepo.save(quizQuestions);
    }

  public QuizQuestions findByTopicName(String topicName) {
    return quizRepo.findByTopicName(topicName);
  }

  public void updateQuizByTopicName(String topicName, QuizQuestions newQuizQuestions) {
    quizRepo.deleteByTopicName(topicName);
    insertNewQuiz(newQuizQuestions);
  }

}
