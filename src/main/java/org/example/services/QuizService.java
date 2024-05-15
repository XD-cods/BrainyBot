package org.example.services;

import org.example.model.Question;
import org.example.model.QuizQuestions;
import org.example.repositories.QuizRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {
  private static List<String> topics = new ArrayList<>();
  private final QuizRepo quizRepo;

  @Autowired
  public QuizService(QuizRepo quizRepo) {
    this.quizRepo = quizRepo;
    topics = quizRepo.findAll().stream().map(QuizQuestions::getTopicName).toList();
  }

  public List<String> getTopics() {
    return topics;
  }

  public void deleteAllQuiz() {
    topics = new ArrayList<>();
    quizRepo.deleteAll();
  }

  public QuizQuestions findByTopicName(String topicName) {
    return quizRepo.findByTopicName(topicName);
  }

  public QuizQuestions findRandomQuestionsByTopicName(String topicName, int count) {
    return quizRepo.findRandomQuestionsByTopicName(topicName, count);
  }

}
