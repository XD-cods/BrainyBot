package org.example.DAO;

import org.example.model.Question;
import org.example.model.QuestionOption;
import org.example.model.Quiz;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class QuizDAO {
  private static final SessionFactory sessionFactory = new Configuration()
          .configure("hibernate.config.xml")
          .buildSessionFactory();

  public static List<Quiz> getAllQuiz() {
    try (Session session = sessionFactory.openSession()) {
      Query<Quiz> query = session.createQuery("from Quiz", Quiz.class);
      List<Quiz> quizzes = query.list();
      if (quizzes == null) {
        return List.of();
      }
      return quizzes;
    }
  }

  public static Quiz getByTopicName(String topicName) {
    try (Session session = sessionFactory.openSession()) {
      Query<Quiz> query = session.createQuery("select topicName from Quiz where topicName = :topicName", Quiz.class);
      query.setParameter("topicName", topicName);
      Quiz quiz = query.getSingleResult();
      if (quiz == null) {
        return new Quiz();
      }
      return quiz;
    }
  }
}
