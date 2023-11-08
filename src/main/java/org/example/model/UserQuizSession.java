package org.example.model;

import java.util.Collection;
import java.util.Iterator;

public class UserQuizSession {

	private int quizCounter = 0;
	private int quizAmount;
	private Iterator<Question> questions;
	private Question currentQuestion;
	private boolean quizMode = true;

	public UserQuizSession(Collection<Question> questions) {
		this.questions = questions.iterator();
		this.quizAmount = questions.size();
		this.currentQuestion = this.questions.next();
	}

	public boolean isQuizMode() {
		return quizMode;
	}

	public void setQuizMode(boolean quizMode) {
		this.quizMode = quizMode;
	}

	public Question getCurrentQuestion() {
		return currentQuestion;
	}

	public void getNextQuestion() {
		currentQuestion = questions.next();
	}

	public boolean isNextQuestionAvailable() {
		return questions.hasNext();
	}

	public int getQuizCounter() {
		return quizCounter;
	}

	public void setQuizCounter(int quizCounter) {
		this.quizCounter = quizCounter;
	}

	public int getQuizAmount() {
		return quizAmount;
	}
	
}
