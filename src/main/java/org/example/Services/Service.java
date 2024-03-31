package org.example.Services;

import org.example.Repositories.QuestionOptionsRepository;
import org.example.Repositories.QuestionRepo;
import org.example.Repositories.QuizRepo;
import org.example.Repositories.UsersRepo;
import org.example.model.PermanentUserInfo;
import org.example.model.Question;
import org.example.model.QuestionOption;
import org.example.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {
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
    return quizRepo.findAllTopicName();
  }

  public void insertNewQuiz(Quiz quiz) {
    quizRepo.save(quiz);
  }

  public void insertNewQuestionOptions(QuestionOption questionOption) {
    questionOptionsRepository.save(questionOption);
  }

  public void insertNewQuestion(Question question) {
    QuestionRepo.save(question);
  }

  public Optional<Quiz> findById(String id){
    return quizRepo.findById(id);
  }
}
