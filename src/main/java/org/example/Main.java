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
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.glassfish.jersey.jaxb.internal.XmlCollectionJaxbProvider;

import java.awt.*;
import java.util.List;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		// Getting bot with helping API token
		TelegramBot bot = new TelegramBot(getToken());
		//Getting qustions from JSON-file
		Question[] quiz = null;
		bot.setUpdatesListener(new UpdatesListener() {
			int counter = 0;
			Question[] quiz;

			@Override
			public int process(List<Update> updates) {
				//getting update from telegram bot
				Update update = updates.get(0);

				if (update.message() != null) {
					Message message = update.message();
					Long chatId = message.chat().id();

					if (update.message().text().equals("/start")) {
						Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup("Test my knowledge", "Read json quiz")
										.oneTimeKeyboard(false)   // optional
										.resizeKeyboard(true)    // optional
										.selective(true);        // optional
						//create starter message
						SendMessage s = new SendMessage(chatId, "Hello! I'm bot for testing your knowledge.");
						s.replyMarkup(replyKeyboardMarkup);
						bot.execute(s);
					}

					if (update.message().text().equals("Read json quiz")) {
						quiz = Question.readJsonQuestions("/home/vlad/Project/knowBot/quiz/question.json");
					}

					if (message.text().equals("Test my knowledge") || message.text().equals("Next question")) {

						//if quiz is not take
						if (quiz == null) {
							SendMessage errorMessage = new SendMessage(chatId, "Pick your quiz");
							bot.execute(errorMessage);
							return UpdatesListener.CONFIRMED_UPDATES_ALL;
						}
						//added at global counter quiz
						counter++;

						if (counter == quiz.length)
							counter = 0;
						//build quiz
						SendPoll quizSend = new SendPoll(chatId, quiz[counter].question, quiz[counter].options)
										.type(Poll.Type.quiz)
										.isAnonymous(false)
										.correctOptionId(quiz[counter].answer);
						//executing bot for sending message
						bot.execute(quizSend);
					}
				}
				//if update is pollAnswer
				else if (update.pollAnswer() != null) {
					Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup("Next question")
									.oneTimeKeyboard(false)   // optional
									.resizeKeyboard(true)    // optional
									.selective(true);        // optional
					Long chatId = update.pollAnswer().user().id();
					Integer quizAnswer = update.pollAnswer().optionIds()[0] + 1;
					//create answer for question
					SendMessage answerMessage = new SendMessage(chatId, "Answer: " + quizAnswer.toString() + ". " + quiz[counter].answerDescription);
					answerMessage.replyMarkup(replyKeyboardMarkup);
					bot.execute(answerMessage);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}


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

	static String getToken() {
		//getting API token from config file
		String token = null;
		Properties prop = new Properties();
		try {
			//load a properties file from class path, inside static method
			prop.load(XmlCollectionJaxbProvider.App.class.getClassLoader().getResourceAsStream("config.properties"));

			//get the property value and print it out
			token = prop.getProperty("token");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return token;
	}
}