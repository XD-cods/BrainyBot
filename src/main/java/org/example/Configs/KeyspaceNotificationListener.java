package org.example.Configs;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class KeyspaceNotificationListener implements MessageListener {

  @Value("${telegram.user.token}")
  private String userToken;

  @Override
  public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
    String msg = new String(message.getBody());
    TelegramBot telegramBot = new TelegramBot(userToken);
    telegramBot.execute(new SendMessage(Long.valueOf(msg), "You've been thinking too long, so you'll have to start over. Write /start or /choice."));
  }
}