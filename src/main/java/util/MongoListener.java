package util;

import org.example.model.QuizQuestions;
import org.example.repositories.QuizRepo;
import org.example.services.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

@Component
public class MongoListener extends AbstractMongoEventListener<QuizQuestions> {
  private QuizRepo quizRepo;

  @Autowired
  public MongoListener(QuizRepo quizRepo) {
    this.quizRepo = quizRepo;
  }

  @Override
  public void onAfterSave(AfterSaveEvent<QuizQuestions> event) {
    System.out.println(event.getSource().getTopicName());
    QuizService.getTopics().add(event.getSource().getTopicName());
  }
}
