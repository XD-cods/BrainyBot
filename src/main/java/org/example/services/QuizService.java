package org.example.services;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.example.model.QuizQuestions;
import org.example.repositories.QuizRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class QuizService {
  private final Lock lock = new ReentrantLock();
  private final Cache<String, List<String>> topicsCache;
  private final QuizRepo quizRepo;

  @Autowired
  public QuizService(QuizRepo quizRepo) {
    this.quizRepo = quizRepo;
    topicsCache = new Cache2kBuilder<String, List<String>>() {}
                          .eternal(true)
                          .build();
    List<String> topics = Arrays.stream(quizRepo.findAllTopic().split(",")).toList();
    topicsCache.put("topics", topics);
  }

  public List<String> getTopics() {
    return topicsCache.get("topics");
  }

  public void addTopic(String topicName){
    lock.lock();
    try {
      List<String> topics = new ArrayList<>(topicsCache.get("topics"));
      topics.add(topicName);
      topicsCache.put("topics", topics);
    } finally {
      lock.unlock();
    }
  }

  public QuizQuestions findRandomQuestionsByTopicName(String topicName, int count) {
    return quizRepo.findRandomQuestionsByTopicName(topicName, count);
  }

  public void updateTopics(){
    List<String> topics = Arrays.stream(quizRepo.findAllTopic().split(",")).toList();
    topicsCache.put("topics", topics);
  }
}
