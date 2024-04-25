package org.example.Configs;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.data.redis.connection.MessageListener;

public class KeyspaceNotificationListener implements MessageListener {

  @Override
  public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
    String msg = new String(message.getBody());
    TelegramBot telegramBot = new TelegramBot("6683005363:AAFHknGfItPK9EeiiwQmeHMb5t5M_lgh-LM");
    telegramBot.execute(new SendMessage(msg,"HI"));
    System.out.println("Received keyspace notification: " + msg);
  }
}