package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.model.Question;
import org.example.model.UserQuizSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotUpdate implements UpdatesListener {

	private final TelegramBot bot;
	private final Map<Long, UserQuizSession> users = new HashMap<>();
	private final QuizReadRepository readRepository;

	public BotUpdate(TelegramBot bot, QuizReadRepository readRepository) {
		this.bot = bot;
		this.readRepository = readRepository;
	}

	@Override
	public int process(List<Update> updates) throws NullPointerException {
		Update update = updates.get(0);
		if (update.callbackQuery() != null) {
			return callbackHandler(update);
		} else if (update.message() == null) {
			return UpdatesListener.CONFIRMED_UPDATES_NONE;
		}

		Message message = update.message();
		Long chatId = message.chat().id();
		String messageText = message.text();

		if (messageText.equals(BotConstants.START_COMMAND)) {
			try {
				users.put(chatId, new UserQuizSession(readRepository.loadQuestions()));
			} catch (IOException e) {
				//todo Logger
				System.out.println(e.getMessage());
				bot.execute(new SendMessage(chatId, BotConstants.ERROR_MESSAGE));
			}
		}

		if (users.containsKey(chatId)) {
			UserQuizSession userQuizSession = users.get(chatId);

			if (userQuizSession.getQuizCounter() != 0) {
				sendAnswer(messageText, userQuizSession, chatId);
				userQuizSession.getNextQuestion();
			}
			sendQuestion(userQuizSession, chatId);
		}

		return UpdatesListener.CONFIRMED_UPDATES_ALL;
	}

	private void sendAnswer(String telegramAnswer, UserQuizSession userQuizSession, Long chatId) {
		Question question = userQuizSession.getCurrentQuestion();
		int questionAnswerNum = question.getAnswer();
		int quizCounter = userQuizSession.getQuizCounter() + 1;
		String answerOption = question.getOptions()[questionAnswerNum];
		String answerDescription = question.getAnswerDescription();
		String answerText;
		if (questionAnswerNum == Integer.parseInt(telegramAnswer)) {
			answerText = "Right! " + "Answer: \"" + answerOption + "\". " + answerDescription;
		} else {
			answerText = "Wrong! " + "Answer: \"" + answerOption + "\". " + answerDescription;
		}

		userQuizSession.setQuizCounter(quizCounter);
		bot.execute(new SendMessage(chatId, answerText));
	}

	private void sendQuestion(UserQuizSession userQuizSession, Long chatId) {
		Question question = userQuizSession.getCurrentQuestion();
		int quizCounter = userQuizSession.getQuizCounter();
		String[] questionOptions = question.getOptions();
		Keyboard inlineKeyboardMarkup = buildInlineKeyboard(questionOptions);
		SendMessage questionMessage = new SendMessage(chatId, String.format("Вопрос: %d\n", quizCounter + 1) + question.getQuestion());
		questionMessage.replyMarkup(inlineKeyboardMarkup);
		bot.execute(questionMessage);
	}

	private InlineKeyboardMarkup buildInlineKeyboard(String[] questionOptions) {
		int optionsLength = questionOptions.length;
		List<InlineKeyboardButton[]> keyboard = new ArrayList<>(optionsLength);
		for (int i = 0; i < optionsLength; i++) {
			InlineKeyboardButton[] row = new InlineKeyboardButton[]{
							new InlineKeyboardButton(questionOptions[i]).callbackData(String.format("%d", i))
			};
			keyboard.add(row);
		}

		return new InlineKeyboardMarkup(keyboard.toArray(new InlineKeyboardButton[][]{}));
	}

	private int callbackHandler(Update update){

		Long chatId = update.callbackQuery().from().id();
		String updateData = update.callbackQuery().data();
		if (users.containsKey(chatId)) {
			UserQuizSession userQuizSession = users.get(chatId);
			sendAnswer(updateData, userQuizSession, chatId);
			userQuizSession.setQuizCounter(userQuizSession.getQuizCounter());
			userQuizSession.getNextQuestion();
			sendQuestion(userQuizSession, chatId);
			return UpdatesListener.CONFIRMED_UPDATES_ALL;
		}
		return UpdatesListener.CONFIRMED_UPDATES_NONE;
	}
}
