package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "quizes", schema = "bot")
public class QuizInfo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer quizId;
    @Column(name = "topic")
    private String topic;
    @Column(name = "quiz_data")
    private String quizData;
    public QuizInfo() {
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getQuizData() {
        return quizData;
    }

    public void setQuizData(String quizData) {
        this.quizData = quizData;
    }
}
