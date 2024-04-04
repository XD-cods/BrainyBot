package org.example.Services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.internal.http2.Hpack;
import org.example.Repositories.QuestionOptionsRepository;
import org.example.Repositories.QuestionRepo;
import org.example.Repositories.QuizRepo;
import org.example.Repositories.UsersRepo;
import org.example.model.PermanentUserInfo;
import org.example.model.Question;
import org.example.model.QuestionOption;
import org.example.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class QuizService {
  @Autowired
  private QuizRepo quizRepo;
  @Autowired
  private QuestionRepo QuestionRepo;
  @Autowired
  private QuestionOptionsRepository questionOptionsRepository;
  @Autowired
  private UsersRepo usersRepo;

  public PermanentUserInfo findByUserName(String userName) {
    return usersRepo.findByUserName(userName);
  }

  public List<String> findAllTopicName() {
    ObjectMapper objectMapper = new ObjectMapper();
    List<String> topics = quizRepo.findAllTopicName();
    if (topics == null) {
      return List.of();
    }
    return topics.stream()
            .map(topic -> {
              try {
                JsonNode jsonNode = objectMapper.readTree(topic);
                return jsonNode.get("topicName").asText();
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            })
            .collect(Collectors.toList());
  }

  public void deleteAllQuiz() {
    quizRepo.deleteAll();
    try {
      Files.delete(Path.of("src/main/resources/topics"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<String> readTopicsFromFile(){
    List<String> allTopics;
    try {
      Path path = Path.of("src/main/resources/topics");
      allTopics =  Files.readAllLines(path);
    } catch (IOException e) {
      return List.of();
    }
    if (allTopics.isEmpty()){
      return List.of();
    }
    return allTopics;
  }

  public void insertNewQuiz(Quiz quiz) {
    if(quizRepo.findByTopicName(quiz.getTopicName()) != null){
    quizRepo.addByTopic(quiz.getTopicName(), quiz.getQuestionList());
    return;
    }
    quizRepo.save(quiz);
    try (FileWriter outputStream = new FileWriter("src/main/resources/topics")) {
      outputStream.write(String.valueOf(findAllTopicName()));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void insertNewQuestionOptions(QuestionOption questionOption) {
    questionOptionsRepository.save(questionOption);
  }

  public void insertNewQuestion(Question question) {
    QuestionRepo.save(question);
  }

  public Optional<Quiz> findById(String id) {
    return quizRepo.findById(id);
  }

  public Quiz findByTopicName(String topicName) {
    return quizRepo.findByTopicName(topicName);
  }

  public void updateQuizByTopicName(String topicName, Quiz newQuiz) {
    quizRepo.deleteByTopicName(topicName);
    insertNewQuiz(newQuiz);
  }
}
