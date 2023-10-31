package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.model.Question;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class QuizReadRepository {

	private final String jsonPath;
	private Collection<Question> quizes;

	public QuizReadRepository(String jsonPath) {
		this.jsonPath = jsonPath;
	}

	public Collection<Question> loadQuestions() throws IOException {
		if (quizes == null) {
			try (BufferedReader reader = new BufferedReader(new FileReader(jsonPath))) {
				Gson gson = new GsonBuilder().create();
				quizes = Arrays.asList(gson.fromJson(reader, Question[].class));
			}
		}
		return quizes;
	}
}
