package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_options")
public class QuestionOption {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "is_answer")
  private boolean isAnswer;

  @Column(name = "option_text")
  private String  optionText;

  @JoinColumn(name = "question_id")
  @ManyToOne
  private Question question;

  public QuestionOption() {

  }

  public QuestionOption(int id, boolean isAnswer, String optionText, Question question) {
    this.id = id;
    this.isAnswer = isAnswer;
    this.optionText = optionText;
    this.question = question;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isAnswer() {
    return isAnswer;
  }

  public void setAnswer(boolean answer) {
    isAnswer = answer;
  }

  public String getOptionText() {
    return optionText;
  }

  public void setOptionText(String optionText) {
    this.optionText = optionText;
  }

  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }
}
