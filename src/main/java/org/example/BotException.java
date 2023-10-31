package org.example;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramException;

public class BotException implements ExceptionHandler {

	@Override
	public void onException(TelegramException e) {
		if (e.response() != null) {
			e.response().errorCode();
			e.response().description();
		} else {
			e.printStackTrace();
		}
	}
}
