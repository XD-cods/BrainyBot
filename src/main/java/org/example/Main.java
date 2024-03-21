package org.example;

import com.pengrad.telegrambot.TelegramBot;
import org.example.DAO.QuizDAO;
import org.example.model.Question;
import org.example.model.QuestionOption;
import org.example.model.Quiz;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Main {
  public static void main(String[] args) throws NullPointerException, IOException {
    TelegramBot bot = new TelegramBot(loadToken());
    QuizRepository readRepository = new QuizRepository("src/main/resources/quizes");
    BotUpdate listener = new BotUpdate(bot, readRepository);
    QuizBotExceptionHandler exception = new QuizBotExceptionHandler();
    bot.setUpdatesListener(listener, exception);
  }

  private static String loadToken() throws IOException {
    Properties prop = new Properties();
    try (InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("config.properties")) {
      prop.load(systemResourceAsStream);
      String token = prop.getProperty("token");
      if (token == null) {
        throw new RuntimeException("Unable to load token");
      }
      return token;
    }
  }
}