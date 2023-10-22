package org.example;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Path;
import java.util.*;
import java.util.Properties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.pengrad.telegrambot.*;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.glassfish.jersey.jaxb.internal.XmlCollectionJaxbProvider;

import java.awt.*;
import java.util.List;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		//getting API token from config file
    String token = null;

    Properties prop = new Properties();
    try {
      //load a properties file from class path, inside static method
      prop.load(XmlCollectionJaxbProvider.App.class.getClassLoader().getResourceAsStream("config.properties"));

      //get the property value and print it out
      token = prop.getProperty("token");
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }

// Creating bot witch helping API token
		TelegramBot bot = new TelegramBot(token);
//Getting qustions from JSON-file
		Question[] question = Question.readJsonQuestions("/home/vlad/Project/knowBot/quiz/quiz.JSON");
		bot.setUpdatesListener(new UpdatesListener() {
			@Override
			public int process(List<Update> updates) {
				//getting update from telegram bot
				Update update = updates.get(0);
				Message updateMessage = update.message();
				Long chatId = updateMessage.chat().id();

				System.out.println(updateMessage.text());
				Scanner scanner = new Scanner(System.in);
				String message = scanner.next();
				SendMessage sender = new SendMessage(chatId, message);

				//executing bot for sending message
				bot.execute(sender);
				return UpdatesListener.CONFIRMED_UPDATES_ALL;
			}
// Create Exception Handler
		}, new ExceptionHandler() {
			public void onException(TelegramException e) {
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