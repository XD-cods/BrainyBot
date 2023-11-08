package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.model.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QuizRepository {
  private final String jsonPath;
  private Collection<Question> quizes;
  private final List<Path> allQuizPath = new ArrayList<>();
  private final List<String> allQuizName = new ArrayList<>();

  public QuizRepository(String jsonPath) {
    this.jsonPath = jsonPath;
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Path.of(jsonPath))) {
      for (Path path : directoryStream) {
        allQuizPath.add(path);
        String fileName = path.getFileName().toString();
        allQuizName.add(fileName.substring(0, fileName.indexOf(".json")));
      }
    } catch (IOException e) {
      System.out.println("Не удалось найти папку квизов!");
    }
  }

  public Collection<Question> loadQuestions(int index) {
    if (quizes == null) {
      try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(allQuizPath.get(index)))) {
        Gson gson = new GsonBuilder().create();
        quizes = Arrays.asList(gson.fromJson(reader, Question[].class));
      } catch (IOException e) {
        e.printStackTrace(System.out);
      }
    }
    return quizes;
  }

  public List<Path> getAllQuizPath() {
    return allQuizPath;
  }

  public int getQuizAmount() {
    return allQuizPath.size();
  }

  public String[] getAllQuizName() {
    return allQuizName.toArray(new String[]{});
  }
}
