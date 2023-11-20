package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
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
  private Collection<Question> quizes;
  private final List<Path> allQuizPath = new ArrayList<>();
  private final List<String> allQuizName = new ArrayList<>();
  private final TelegramBot bot;

  public QuizRepository(String jsonPath, TelegramBot bot) {
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Path.of(jsonPath))) {
      for (Path path : directoryStream) {
        allQuizPath.add(path);
        String fileName = path.getFileName().toString();
        allQuizName.add(fileName.substring(0, fileName.indexOf(".json")));
      }
    } catch (IOException e) {
      throw new RuntimeException("Unable to read quizes", e);
    }
    this.bot = bot;
  }

  public Collection<Question> loadQuestions(int index, Long chatId) {
    try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(allQuizPath.get(index)))) {
      Gson gson = new GsonBuilder().create();
      quizes = Arrays.asList(gson.fromJson(reader, Question[].class));
    } catch (IOException e) {
      bot.execute(new SendMessage(chatId, BotConstants.ERROR_MESSAGE));
      throw new RuntimeException("Unable to load quiz", e);
    }
    return quizes;
  }

  public String[] getAllQuizName() {
    return allQuizName.toArray(new String[]{});
  }

  public String getQuizName(int index) {
    return allQuizName.get(index);
  }
}
