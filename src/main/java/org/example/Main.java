package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import org.example.Services.Service;
import org.example.model.Question;
import org.example.model.QuestionOption;
import org.example.model.Quiz;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;


public class Main {
  public static void main(String[] args) throws NullPointerException, IOException {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(org.example.Configs.MongoConfig.class);
    Service service = applicationContext.getBean(Service.class);
    TelegramBot bot = new TelegramBot(loadToken());
    TelegramBot adminBot = new TelegramBot(loadAdminToken());
    AdminBot adminListener = new AdminBot(adminBot, service);
    QuizBotExceptionHandler exception = new QuizBotExceptionHandler();
    adminBot.setUpdatesListener(adminListener, exception);
//    bot.setUpdatesListener(listener, exception);
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