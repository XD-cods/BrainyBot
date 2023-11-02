package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.pengrad.telegrambot.*;

public class Main {
	public static void main(String[] args) throws NullPointerException, IOException {
		TelegramBot bot = new TelegramBot(loadToken());
		QuizRepository readRepository = new QuizRepository("src/main/resources/quiz's");
		BotUpdate listener = new BotUpdate(bot, readRepository);
		bot.setUpdatesListener(listener, new BotException());
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