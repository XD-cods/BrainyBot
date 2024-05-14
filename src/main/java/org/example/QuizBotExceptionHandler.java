package org.example;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QuizBotExceptionHandler implements ExceptionHandler {


	private static final Logger log = LogManager.getLogger(QuizBotExceptionHandler.class);

	public QuizBotExceptionHandler() {
	}

	@Override
	public void onException(TelegramException e) {
		if (e.response() != null) {
			e.response().errorCode();
			e.response().description();
		} else {
			log.fatal("Was throw exception in class QuizBotExceptionHandler");
			throw new RuntimeException("Don't access to bot", e);
		}
	}
}
