package org.example;

import com.pengrad.telegrambot.TelegramBot;
import org.example.Configs.MongoDBConfig;
import org.example.Configs.RedisConfig;
import org.example.Services.QuizService;
import org.example.Services.RedisService;
import org.example.Services.UsersService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Main {
//    static String userToken = System.getenv("TELEGRAM_USER_TOKEN");
//    static String adminToken = System.getenv("TELEGRAM_ADMIN_TOKEN");
    static String userToken;
    static String adminToken;
  static {
    try {
      userToken = loadToken();
      adminToken = loadAdminToken();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws NullPointerException, IOException {

    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MongoDBConfig.class);
    ApplicationContext redisApplicationContext = new AnnotationConfigApplicationContext(RedisConfig.class);
    RedisService redisService = redisApplicationContext.getBean(RedisService.class);
    QuizService quizService = applicationContext.getBean(QuizService.class);
    UsersService usersService = applicationContext.getBean(UsersService.class);
    quizService.updateTopicsFile();
    TelegramBot bot = new TelegramBot(userToken);
    TelegramBot adminBot = new TelegramBot(adminToken);
    AdminBot adminListener = new AdminBot(adminBot, quizService, usersService);
    BotUpdate listener = new BotUpdate(bot, quizService, usersService, redisService);
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