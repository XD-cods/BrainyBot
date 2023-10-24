package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Question {
	String[] options;
	String question;
	int answer;
	String answerDescription;

	static Question[] readJsonQuestions(String jsonPath) {
		try (BufferedReader reader = new BufferedReader(new FileReader(jsonPath))) {
			Gson gson = new GsonBuilder().create();
			Question[] questions = gson.fromJson(reader, Question[].class);
			return questions;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
