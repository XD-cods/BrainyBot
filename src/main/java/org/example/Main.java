package org.example;

import java.util.*;
import com.pengrad.telegrambot.*;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

import java.awt.*;
import java.io.Console;
import java.util.List;

public class Main {
  public static void main(String[] args) {
// Create your bot passing the token received from @BotFather
    TelegramBot bot = new TelegramBot("API");
    bot.setUpdatesListener(new UpdatesListener() {
      @Override
      public int process(List<Update> updates) {
        //getting update from telegram bot
        Update update = updates.get(0);
        Message updateMessage = update.message();
        Long chatId = updateMessage.chat().id();

        System.out.println(updateMessage.text());
        Scanner scanner = new Scanner(System.in);
        String  message = scanner.next();
        SendMessage sender = new SendMessage(chatId,message);

        //executing bot for sending message
        bot.execute(sender);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
      }
// Create Exception Handler
    }, new ExceptionHandler() {
      public void onException(TelegramException e)
      {
        if (e.response() != null) {
          // got bad response from telegram
          e.response().errorCode();
          e.response().description();
        } else {
          // probably network error
          e.printStackTrace();
        }
      }
    });
// Send messages
  }
}