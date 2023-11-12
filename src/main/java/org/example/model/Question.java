package org.example.model;

public class Question {
	private String question;
	private String[] options;
	private int answer;
	private String answerDescription;

	public String[] getOptions() {
		return options;
	}

	public String getQuestion() {
		return question;
	}

	public int getAnswer() {
		return answer;
	}

	public String getAnswerDescription() {
		return answerDescription;
	}

}
