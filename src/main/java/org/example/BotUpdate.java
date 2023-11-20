package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.model.Question;
import org.example.model.UserInfo;
import org.example.model.UserQuizSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotUpdate implements UpdatesListener {

  private final TelegramBot bot;
  private final Map<Long, UserInfo> users = new HashMap<>();
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
      if (!isRegisterUser(chatId)) {
        clearKeyboard(update, chatId);
        bot.execute(new SendMessage(chatId, "Input /start"));
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
      }

      UserInfo userInfo = users.get(chatId);
      if (userInfo.isChoiceQuiz()) {
        choiceQuiz(update, userInfo, chatId);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
      } else if (userInfo.getUserQuizSession() == null) {
        bot.execute(new SendMessage(chatId, "Input /start_quiz for start/restart quiz or for choice quiz /choice"));
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
      }

      callbackHandler(update, userInfo, chatId);
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    } else if (update.message() == null) {
      return UpdatesListener.CONFIRMED_UPDATES_NONE;
    }

    Message message = update.message();
    Long chatId = message.chat().id();
    String messageText = message.text();
    UserInfo userInfo = null;
    UserQuizSession userQuizSession = null;

    if (isRegisterUser(chatId)) {
      userInfo = users.get(chatId);
      userQuizSession = userInfo.getUserQuizSession();
    }

    switch (messageText) {
      case BotConstants.REGISTER_COMMAND -> {
        if (userInfo != null) {
          break;
        }
        bot.execute(new SendMessage(chatId, BotConstants.STARTING_MESSAGE));
        userInfo = new UserInfo();
        users.put(chatId, userInfo);
        break;
      }

      case BotConstants.START_COMMAND -> {
        if (userInfo == null) {
          bot.execute(new SendMessage(chatId, "Input /start"));
          break;
        } else if (userQuizSession != null && userQuizSession.isQuizMode()) {
          bot.execute(new SendMessage(chatId, "End your quiz"));
          break;
        }

        int quizIndex = userInfo.getQuizIndex();
        userQuizSession = new UserQuizSession(readRepository.loadQuestions(quizIndex, chatId));
        userInfo.setUserQuizSession(userQuizSession);
        bot.execute(new SendMessage(chatId, "Quiz: " + readRepository.getQuizName(quizIndex)));
        sendQuestion(userQuizSession, chatId);
        break;
      }

      case BotConstants.CHOICE_COMMAND -> {
        if (userInfo == null) {
          bot.execute(new SendMessage(chatId, "Input /start"));
          break;
        } else if (userQuizSession != null && userQuizSession.isQuizMode()) {
          bot.execute(new SendMessage(chatId, "End your quiz"));
          break;
        }
        sendChoiceQuiz(chatId);
        break;
      }
    }
    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }

  private void choiceQuiz(Update update, UserInfo userInfo, Long chatId) {
    int quizIndex = Integer.parseInt(update.callbackQuery().data());
    userInfo.setQuizIndex(quizIndex);
    userInfo.setChoiceQuiz(false);
    bot.execute(new SendMessage(chatId, "Input /start_quiz"));
    clearKeyboard(update, chatId);
  }

  private void sendChoiceQuiz(Long chatId) {
    users.get(chatId).setChoiceQuiz(true);
    String choiceQuizText = "Choose your quiz!";
    String[] allQuizName = readRepository.getAllQuizName();
    for (int i = 0; i < allQuizName.length; i++) {
      choiceQuizText += String.format("\n%d. %s", i + 1, allQuizName[i]);
    }
    SendMessage choiceQuiz = new SendMessage(chatId, choiceQuizText);
    Keyboard keyboard = buildInlineKeyboard(readRepository.getAllQuizName().length);
    choiceQuiz.replyMarkup(keyboard);
    bot.execute(choiceQuiz);
  }

  private void sendQuestion(UserQuizSession userQuizSession, Long chatId) {
    Question question = userQuizSession.getCurrentQuestion();
    int quizCounter = userQuizSession.getQuizCounter() + 1;
    String[] questionOptions = question.getOptions();
    Keyboard inlineKeyboardMarkup = buildInlineKeyboard(questionOptions.length);
    String questionTextMessage = String.format("❓ Question: %d\n%s\n", quizCounter, question.getQuestion());
    for (int i = 0; i < questionOptions.length; i++) {
      questionTextMessage += String.format("\n%d. %s", i + 1, questionOptions[i]);
    }
    SendMessage questionMessage = new SendMessage(chatId, questionTextMessage);
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

  private void callbackHandler(Update update, UserInfo userInfo, Long chatId) {
    UserQuizSession userQuizSession = userInfo.getUserQuizSession();
    String updateData = update.callbackQuery().data();
    if (userQuizSession.isQuizMode()) {
      sendAnswer(updateData, userQuizSession, chatId);
      clearKeyboard(update, chatId);
    }

    if (userQuizSession.getQuizCounter() == userQuizSession.getQuizAmount()) {
      userQuizSession.setQuizMode(false);
      sendQuizStats(userQuizSession, chatId);
      bot.execute(new SendMessage(chatId, "Input /start_quiz to reset bot or chose quiz another quiz /choice"));
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

  private InlineKeyboardMarkup buildInlineKeyboard(int keyboardLength) {
    List<InlineKeyboardButton[]> inlineKeyboardButtons = new ArrayList<>();
    int column = 0;
    List<InlineKeyboardButton> rows = new ArrayList<>();
    for (int i = 0; i < keyboardLength; i++) {
      if (i % 2 == 0) {
        column = 0;
        inlineKeyboardButtons.add(rows.toArray(new InlineKeyboardButton[]{}));
        rows = new ArrayList<>();
      }
      rows.add(new InlineKeyboardButton(String.valueOf(i + 1)).callbackData(String.valueOf(i)));
      column++;
    }
    inlineKeyboardButtons.add(rows.toArray(new InlineKeyboardButton[]{}));
    return new InlineKeyboardMarkup(inlineKeyboardButtons.toArray(new InlineKeyboardButton[][]{}));
  }

  private void clearKeyboard(Update update, Long chatId) {
    Message message = update.callbackQuery().message();
    EditMessageText editMessage = new EditMessageText(chatId, message.messageId(), message.text());
    editMessage.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("").callbackData("deleted")));
    bot.execute(editMessage);
  }

  private boolean isRegisterUser(Long chatId) {
    return users.containsKey(chatId);
  }
}
