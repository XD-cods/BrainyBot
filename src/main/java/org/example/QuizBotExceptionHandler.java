package org.example;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramException;

public class QuizBotExceptionHandler implements ExceptionHandler {
	public QuizBotExceptionHandler() {
	}

	@Override
	public void onException(TelegramException e) {
		//todo logger
		if (e.response() != null) {
			e.response().errorCode();
			e.response().description();
		} else {
			throw new RuntimeException("Don't access to bot", e);
		}
	}
}
