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
	private final QuizRepository readRepository;

	public BotUpdate(TelegramBot bot, QuizRepository readRepository) {
		this.bot = bot;
		this.readRepository = readRepository;
	}

	@Override
	public int process(List<Update> updates) throws NullPointerException {
		Update update = updates.get(updates.size() - 1);
		if (update.callbackQuery() != null) {
			Long chatId = update.callbackQuery().from().id();
			if (users.containsKey(chatId)) {
				UserQuizSession userQuizSession = users.get(chatId);
				if (userQuizSession.isQuizMode()) {
					return callbackHandler(update, userQuizSession, chatId);
				} else {
					try {
						readRepository.loadQuestions(Integer.parseInt(update.callbackQuery().data()));
						userQuizSession.setQuizMode(true);
						sendQuestion(userQuizSession,chatId);
						return UpdatesListener.CONFIRMED_UPDATES_ALL;
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		if (update.message() == null) {
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
			sendChoiceQuiz(chatId);
		}

		return UpdatesListener.CONFIRMED_UPDATES_ALL;
	}

	private InlineKeyboardMarkup buildInlineKeyboard(String[] keyboardButtonName) {
		int keyboardButtonLength = keyboardButtonName.length;
		List<InlineKeyboardButton[]> inlineKeyboardButtons = new ArrayList<>(keyboardButtonLength);
		for (int i = 0; i < keyboardButtonLength; i++) {
			InlineKeyboardButton[] row = new InlineKeyboardButton[]{new InlineKeyboardButton(keyboardButtonName[i]).callbackData(String.format("%d", i))};
			inlineKeyboardButtons.add(row);
		}

		return new InlineKeyboardMarkup(inlineKeyboardButtons.toArray(new InlineKeyboardButton[][]{}));
	}

	private void sendChoiceQuiz(Long chatId) {
		SendMessage choiceQuiz = new SendMessage(chatId, "Choose your quiz!");
		choiceQuiz.replyMarkup(buildInlineKeyboard(readRepository.getAllQuizName()));
		bot.execute(choiceQuiz);
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

	private int callbackHandler(Update update, UserQuizSession userQuizSession, Long chatId) {
		String updateData = update.callbackQuery().data();
		if (userQuizSession.isQuizMode()) {
			sendAnswer(updateData, userQuizSession, chatId);
		}

		if (userQuizSession.isNextQuestionAvailable()) {
			userQuizSession.getNextQuestion();
			sendQuestion(userQuizSession, chatId);
		}

		if (userQuizSession.getQuizCounter() == userQuizSession.getQuizAmount()) {
			userQuizSession.setQuizMode(false);
			bot.execute(new SendMessage(chatId, "Введите /start что-бы перезапустить бота"));
		}

		return UpdatesListener.CONFIRMED_UPDATES_ALL;
	}
}
