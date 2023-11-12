package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.model.Question;
import org.example.model.UserQuizSession;

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
        callbackHandler(update, userQuizSession, chatId);
      }
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    if (update.message() == null) {
      return UpdatesListener.CONFIRMED_UPDATES_NONE;
    }

    Message message = update.message();
    Long chatId = message.chat().id();
    String messageText = message.text();

    if (messageText.equals(BotConstants.START_COMMAND)) {
      bot.execute(new SendMessage(chatId, BotConstants.STARTING_MESSAGE));
      users.put(chatId, new UserQuizSession(readRepository.loadQuestions(0)));
      sendQuestion(users.get(chatId), chatId);
    }

    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }

  private void sendChoiceQuiz(Long chatId) {
    SendMessage choiceQuiz = new SendMessage(chatId, "Choose your quiz!");
    choiceQuiz.replyMarkup(buildInlineKeyboard(readRepository.getAllQuizName()));
    bot.execute(choiceQuiz);
  }

  private void sendQuestion(UserQuizSession userQuizSession, Long chatId) {
    Question question = userQuizSession.getCurrentQuestion();
    int quizCounter = userQuizSession.getQuizCounter();
    String[] questionOptions = question.getOptions();
    Keyboard inlineKeyboardMarkup = buildInlineKeyboard(questionOptions);
    SendMessage questionMessage = new SendMessage(chatId, String.format("❓ Question: %d\n%s", quizCounter + 1, question.getQuestion()));
    questionMessage.replyMarkup(inlineKeyboardMarkup);
    bot.execute(questionMessage);
  }

  private void sendAnswer(String telegramAnswer, UserQuizSession userQuizSession, Long chatId) {
    int userAnswerNum = Integer.parseInt(telegramAnswer);
    String answerText = getAnswerText(userQuizSession, userAnswerNum);
    userQuizSession.addQuizCounter();
    bot.execute(new SendMessage(chatId, answerText).parseMode(ParseMode.HTML));
  }

  private static String getAnswerText(UserQuizSession userQuizSession, int userAnswerNum) {
    Question question = userQuizSession.getCurrentQuestion();
    int questionAnswerNum = question.getAnswer();
    String userAnswerText = question.getOptions()[userAnswerNum];
    String quizAnswerOption = question.getOptions()[questionAnswerNum];
    String quizAnswerDescription = question.getAnswerDescription();
    String answerMessageText;
    if (questionAnswerNum == userAnswerNum) {
      answerMessageText = String.format("✅ It's Right!\n\n<b>Answer:</b> %s\n\n%s", quizAnswerOption, quizAnswerDescription);
      userQuizSession.addRightCounter();
    } else {
      answerMessageText = String.format("❌ It's wrong!\n\n<b>Your answer:</b> %s\n<b>Right answer:</b> %s\n\n%s", userAnswerText, quizAnswerOption, quizAnswerDescription);
    }
    return answerMessageText;
  }

  private void sendQuizStats(UserQuizSession userQuizSession, Long chatId) {
    int quizAmount = userQuizSession.getQuizAmount();
    int wrongAnswerCounter = userQuizSession.getWrongAnswerCounter();
    int rightAnswerCounter = userQuizSession.getRightAnswerCounter();
    String statMessage = String.format("❓ <b>Question number:</b> %d\n\n✅ <b>Right answers:</b> %d\n\n❌ <b>Wrong answers:</b> %d", quizAmount, rightAnswerCounter, wrongAnswerCounter);
    bot.execute(new SendMessage(chatId, statMessage).parseMode(ParseMode.HTML));
  }

  private void callbackHandler(Update update, UserQuizSession userQuizSession, Long chatId) {
    String updateData = update.callbackQuery().data();
    if (userQuizSession.isQuizMode()) {
      sendAnswer(updateData, userQuizSession, chatId);
      clearKeyboard(update, chatId);
    }

    if (userQuizSession.getQuizCounter() == userQuizSession.getQuizAmount()) {
      userQuizSession.setQuizMode(false);
      sendQuizStats(userQuizSession, chatId);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      bot.execute(new SendMessage(chatId, "Input /start to reset bot"));
    }

    if (userQuizSession.isNextQuestionAvailable()) {
      userQuizSession.getNextQuestion();
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      sendQuestion(userQuizSession, chatId);
    }
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

  private void clearKeyboard(Update update, Long chatId) {
    Message message = update.callbackQuery().message();
    EditMessageText editMessage = new EditMessageText(chatId, message.messageId(), message.text());
    editMessage.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("").callbackData("deleted")));
    bot.execute(editMessage);
  }
}
