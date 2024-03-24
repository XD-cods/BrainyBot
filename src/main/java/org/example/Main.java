package org.example;

import com.pengrad.telegrambot.TelegramBot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Main {
  public static void main(String[] args) throws NullPointerException, IOException {
    TelegramBot bot = new TelegramBot(loadToken());
    DAORepository readRepository = new DAORepository();
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