package org.example;

import com.pengrad.telegrambot.TelegramBot;
import org.example.Services.QuizService;
import org.example.Services.UsersService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Main {
  public static void main(String[] args) throws NullPointerException, IOException {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(org.example.Configs.MongoConfig.class);
    QuizService quizService = applicationContext.getBean(QuizService.class);
    UsersService usersService = applicationContext.getBean(UsersService.class);
    quizService.readTopicsFromFile().forEach(System.out::println);
    TelegramBot bot = new TelegramBot(loadToken());
    TelegramBot adminBot = new TelegramBot(loadAdminToken());
    AdminBot adminListener = new AdminBot(adminBot, quizService, usersService);
    BotUpdate listener = new BotUpdate(bot, quizService, usersService);
    QuizBotExceptionHandler exception = new QuizBotExceptionHandler();
    adminBot.setUpdatesListener(adminListener, exception);
    bot.setUpdatesListener(listener, exception);
  }

  private static String loadAdminToken() throws IOException {
    Properties prop = new Properties();
    try (InputStream systemResourceAsStream = ClassLoader.getSystemResourceAsStream("config.properties")) {
      prop.load(systemResourceAsStream);
      String token = prop.getProperty("AdminToken");
      if (token == null) {
        throw new RuntimeException("Unable to load token");
      }
      return token;
    }
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