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
		Question[] questions;
		String json = "";
		try (BufferedReader bf = new BufferedReader(new FileReader(jsonPath))) {
			String line;
			while ((line = bf.readLine()) != null) {
				json += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Gson gson = new GsonBuilder().create();
		questions = gson.fromJson(json, Question[].class);
		return questions;
	}
}
