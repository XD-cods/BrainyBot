package org.example;

import com.pengrad.telegrambot.TelegramBot;
import org.example.configs.MongoDBConfig;
import org.example.configs.RedisConfig;
import org.example.services.QuizService;
import org.example.services.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.properties")
public class Main {
  @Value("${telegram.user.token}")
  private String userToken;
  @Value("${telegram.admin.token}")
  private String adminToken;

  public static void main(String[] args) throws NullPointerException {
    ApplicationContext context = new AnnotationConfigApplicationContext(Main.class,
            RedisConfig.class, MongoDBConfig.class);
    context.getBean(Main.class).run(context);
  }

  private void run(ApplicationContext context) {
    RedisService redisService = context.getBean(RedisService.class);
    QuizService quizService = context.getBean(QuizService.class);
    TelegramBot userBot = new TelegramBot(userToken);
    TelegramBot adminBot = new TelegramBot(adminToken);
    QuizBotListener userListener = new QuizBotListener(userBot, quizService);
    AdminBot adminListener = new AdminBot(adminBot, quizService, redisService);
    QuizBotExceptionHandler exception = new QuizBotExceptionHandler();
    adminBot.setUpdatesListener(adminListener, exception);
    userBot.setUpdatesListener(userListener, exception);
  }
}