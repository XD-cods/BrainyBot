package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.QuizQuestions;
import org.example.repositories.QuizRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {
  private static List<String> topics = new ArrayList<>();
  private final QuizRepo quizRepo;

  @Autowired
  public QuizService(QuizRepo quizRepo) {
    this.quizRepo = quizRepo;
    topics = quizRepo.findAllTopicName();
  }

  public List<String> getTopics() {
    ObjectMapper objectMapper = new ObjectMapper();
    List<String> topic = quizRepo.findAllTopicName();
    if (topic == null) {
      return List.of();
    }
    return topics = topic.stream().map(string -> {
      try {
        JsonNode jsonNode = objectMapper.readTree(string);
        return jsonNode.get("topicName").asText();
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }).collect(Collectors.toList());
  }

  public void deleteAllQuiz() {
    topics = new ArrayList<>();
    quizRepo.deleteAll();
  }

  public QuizQuestions findByTopicName(String topicName) {
    return quizRepo.findByTopicName(topicName);
  }

}
