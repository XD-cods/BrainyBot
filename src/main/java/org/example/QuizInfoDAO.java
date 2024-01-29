package org.example;

import org.example.model.QuizInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;


public class QuizInfoDAO {
    private static SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

    public List<QuizInfo> getAllQuizInfo() {
        try (Session session = sessionFactory.openSession()) {
            Query<QuizInfo> query = session.createQuery("from QuizInfo", QuizInfo.class);
            return query.list();
        }
    }

    public List<String> getTopicsName() {
        try (Session session = sessionFactory.openSession()) {
            Query<String> query = session.createQuery("select topic from QuizInfo", String.class);
            return query.list();
        }
    }

    public QuizInfo getQuizInfo(String topicName) {
        try (Session session = sessionFactory.openSession()) {
            Query<QuizInfo> query = session.createQuery("from QuizInfo where topic = :topicName",
                    QuizInfo.class);
            query.setParameter("topicName",topicName);
            return query.getSingleResultOrNull();
        }
    }
}
