package org.example.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "questions")
public class Question {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "question_text")
  private String question;

  @Column(name = "answer_description")
  private String answerDescription;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "question_id")
  private List<QuestionOption> optionList;

  public Question() {
  }

  public Question(String question, List<QuestionOption> questionOptions, String answerDescription) {
    this.question = question;
    this.answerDescription = answerDescription;
    this.optionList = questionOptions;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getAnswerDescription() {
    return answerDescription;
  }

  public void setAnswerDescription(String answerDescription) {
    this.answerDescription = answerDescription;
  }

  public List<QuestionOption> getOptionList() {
    return optionList;
  }

  public void setOptionList(List<QuestionOption> optionList) {
    this.optionList = optionList;
  }


}
