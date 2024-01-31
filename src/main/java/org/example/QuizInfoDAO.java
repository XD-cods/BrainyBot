package org.example;

import com.google.gson.Gson;
import org.example.model.QuizInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;


public  class QuizInfoDAO {
    private static final SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

    public static List<QuizInfo> getAllQuizInfo() {
        try (Session session = sessionFactory.openSession()) {
            Query<QuizInfo> query = session.createQuery("from QuizInfo", QuizInfo.class);
            return query.list();
        }
    }

    public static List<String> getTopicsName() {
        try (Session session = sessionFactory.openSession()) {
            Query<String> query = session.createQuery("select topic from QuizInfo", String.class);
            return query.list();
        }
    }

    public static QuizInfo getQuizInfo(String topicName) {
        try (Session session = sessionFactory.openSession()) {
            Query<QuizInfo> query = session.createQuery("from QuizInfo where topic = :topicName",
                    QuizInfo.class);
            query.setParameter("topicName",topicName);
            return query.getSingleResultOrNull();
        }
    }

    public static void addQuizInfo(String topicName, String quizData){
        try(Session session = sessionFactory.openSession()){
            QuizInfo quizInfo = new QuizInfo();

            quizInfo.setTopic(topicName);
            quizInfo.setQuizData(quizData);
            quizInfo = session.merge(quizInfo);
            session.beginTransaction();
            session.persist(quizInfo);
            session.getTransaction().commit();
        }
    }

    public static void deleteQuizInfo(String topicName){
        try(Session session = sessionFactory.openSession()){
            Query<QuizInfo> query = session.createQuery("from QuizInfo where topic = :topicName", QuizInfo.class);
            query.setParameter("topicName",topicName);
            QuizInfo quizInfo = query.getSingleResultOrNull();
            if(quizInfo != null) {
                session.beginTransaction();
                session.remove(quizInfo);
                session.getTransaction().commit();
            }
        }
    }
}
