package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.pengrad.telegrambot.*;

public class Main {
  public static void main(String[] args) throws NullPointerException, IOException {
    TelegramBot bot = new TelegramBot(loadToken());
    QuizRepository readRepository = new QuizRepository("src/main/resources/quizes", bot);
    BotUpdate listener = new BotUpdate(bot, readRepository);
    BotException exception = new BotException();
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