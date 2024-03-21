package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.model.Question;
import org.example.model.UserInfo;
import org.example.model.UserQuizSession;

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
      return handleCallback(update.callbackQuery());
    } else if (update.message() == null) {
      return UpdatesListener.CONFIRMED_UPDATES_NONE;
    }

    Message message = update.message();
    Long chatId = message.chat().id();
    String messageText = message.text();
    if (!isRegisterUser(chatId) && !messageText.equals(BotConstants.START_BOT_COMMAND)) {
      bot.execute(new SendMessage(chatId, "input /start"));
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    switch (messageText) {
      case BotConstants.START_BOT_COMMAND -> {
        if (isRegisterUser(chatId)) {
          break;
        }
        bot.execute(new SendMessage(chatId, BotConstants.STARTING_MESSAGE));
        UserInfo userInfo = new UserInfo();
        users.put(chatId, userInfo);
      }
      case BotConstants.CHOOSE_TOPIC_COMMAND -> {
        UserInfo userInfo = users.get(chatId);
        UserQuizSession userQuizSession = userInfo.getUserQuizSession();
        if (userQuizSession != null) {
          bot.execute(new SendMessage(chatId, "Please finish this quiz"));
          break;
        }
        if (userInfo.isTopicChosen()) {
          bot.execute(new SendMessage(chatId, "You are not chosen quiz"));
          break;
        }
        sendChoiceQuiz(chatId);
      }
      case BotConstants.START_QUIZ_COMMAND -> {
        UserInfo userInfo = users.get(chatId);
        UserQuizSession userQuizSession = userInfo.getUserQuizSession();
        if (userQuizSession != null) {
          bot.execute(new SendMessage(chatId, "Please finish this quiz"));
          break;
        }

        String currentTopicName = userInfo.getCurrentTopicName();
        if (currentTopicName == null) {
          bot.execute(new SendMessage(chatId, "Topic is not chosen, please use /choice command to choose"));
          break;
        }
        if (userInfo.isTopicChosen()) {
          bot.execute(new SendMessage(chatId, "You are not chosen quiz"));
          break;
        }
        userQuizSession = new UserQuizSession(readRepository.loadQuestions(currentTopicName));
        userInfo.setUserQuizSession(userQuizSession);
        bot.execute(new SendMessage(chatId, "Quiz: " + userInfo.getCurrentTopicName()));
        sendQuestion(chatId);
      }
      case BotConstants.CANCEL_QUIZ_COMMAND -> {
        UserInfo userInfo = users.get(chatId);
        UserQuizSession userQuizSession = userInfo.getUserQuizSession();
        if (userQuizSession == null) {
          bot.execute(new SendMessage(chatId, "You didn't start the quiz"));
          break;
        }
        clearLastMessageKeyboard(chatId);
        sendStatsCanceledQuiz(chatId);
      }
    }
    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private void sendChoiceQuiz(Long chatId) {
    users.get(chatId).setChoiceQuiz(true);
    SendMessage choiceQuiz = new SendMessage(chatId, "Choose your quiz!");
    Keyboard keyboard = buildTopicChoiceKeyboard();
    choiceQuiz.replyMarkup(keyboard);
    users.get(chatId).setLastBotMessage(bot.execute(choiceQuiz).message());
  }

  private void sendQuestion(Long chatId) {
    UserQuizSession userQuizSession = users.get(chatId).getUserQuizSession();
    Question question = userQuizSession.getNextQuestion();
    String[] questionOptions = question.getOptions();
    Keyboard inlineKeyboardMarkup = buildInlineKeyboard(questionOptions.length);
    StringBuilder questionTextMessage = new StringBuilder(String.format("❓ Question: %d\n%s\n",
            userQuizSession.getQuestionCounter(), question.getQuestion()));
    for (int i = 0; i < questionOptions.length; i++) {
      questionTextMessage.append(String.format("\n%d. %s", i + 1, questionOptions[i]));
    }

    SendMessage questionMessage = new SendMessage(chatId, questionTextMessage.toString());
    questionMessage.replyMarkup(inlineKeyboardMarkup);
    users.get(chatId).setLastBotMessage(bot.execute(questionMessage).message());
  }

  private void sendAnswer(CallbackQuery callbackQuery, Long chatId) {
    clearLastMessageKeyboard(chatId);
    int userAnswerNum = Integer.parseInt(callbackQuery.data());
    UserQuizSession userQuizSession = users.get(chatId).getUserQuizSession();
    String answerText = getAnswerText(userQuizSession, userAnswerNum);
    SendMessage answerMessage = new SendMessage(chatId, answerText).parseMode(ParseMode.HTML);
    users.get(chatId).setLastBotMessage(bot.execute(answerMessage).message());
  }

  private void sendQuizStats(Long chatId) {
    UserQuizSession userQuizSession = users.get(chatId).getUserQuizSession();
    int quizAmount = userQuizSession.getQuestionAmount();
    int rightAnswerCounter = userQuizSession.getRightAnswerCounter();
    String statMessageText = getQuizStatText(quizAmount, rightAnswerCounter);
    userQuizSession.setQuizMode(false);
    SendMessage statMessage = new SendMessage(chatId, statMessageText).parseMode(ParseMode.HTML);
    Message lastBotMessage = bot.execute(statMessage).message();
    users.get(chatId).setLastBotMessage(lastBotMessage);
    users.get(chatId).setUserQuizSession(null);
  }

  private static String getQuizStatText(int quizAmount, int rightAnswerCounter) {
    return String.format("❓ <b>Question number:</b> %d\n\n" +
                    "✅ <b>Right answers:</b> %d\\%d\n\n" +
                    "Input /start_quiz to reset bot or chose quiz another quiz /choice",
            quizAmount, rightAnswerCounter, quizAmount);
  }

  private void sendStatsCanceledQuiz(Long chatId) {
    UserQuizSession userQuizSession = users.get(chatId).getUserQuizSession();
    int questionCount = userQuizSession.getQuestionCounter();
    int rightAnswerCounter = userQuizSession.getRightAnswerCounter();
    String statMessageText = getCanceledQuizStatText(questionCount, rightAnswerCounter);
    userQuizSession.setQuizMode(false);
    SendMessage statMessage = new SendMessage(chatId, statMessageText).parseMode(ParseMode.HTML);
    users.get(chatId).setLastBotMessage(bot.execute(statMessage).message());
    users.get(chatId).setUserQuizSession(null);
  }

  private static String getCanceledQuizStatText(int questionCount, int rightAnswerCounter) {
    String statMessageText = String.format("❓<b>You canceled quiz</b>\n\n<b>The questions were:</b> %d\n\n" +
                    "✅ <b>Right answers:</b> %d\\%d\n\n" +
                    "Input /start_quiz to reset bot or chose quiz another quiz /choice",
            questionCount, rightAnswerCounter, questionCount);
    return statMessageText;
  }

  private int handleCallback(CallbackQuery callbackQuery) {
    Long userId = callbackQuery.from().id();
    if (!isRegisterUser(userId)) {
      bot.execute(new SendMessage(userId, "Input /start"));
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    String callbackData = callbackQuery.data();
    if (callbackData == null) {
      return UpdatesListener.CONFIRMED_UPDATES_NONE;
    }
    if (callbackData.startsWith("topicChoice:")) {

      chooseQuiz(callbackData, userId);
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    handlerQuizAnswer(callbackQuery, userId);
    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }

  private void handlerQuizAnswer(CallbackQuery callbackQuery, Long chatId) {
    UserInfo userInfo = users.get(chatId);
    UserQuizSession userQuizSession = userInfo.getUserQuizSession();
    if (userQuizSession.isQuizMode()) {
      sendAnswer(callbackQuery, chatId);
    }

    if (userQuizSession.getQuestionCounter() == userQuizSession.getQuestionAmount()) {
      sendQuizStats(chatId);
    }

    if (userQuizSession.isNextQuestionAvailable()) {
      sendQuestion(chatId);
    }
  }

  private InlineKeyboardMarkup buildInlineKeyboard(int keyboardLength) {
    List<InlineKeyboardButton[]> inlineKeyboardButtons = new ArrayList<>();
    List<InlineKeyboardButton> rows = new ArrayList<>();
    for (int i = 0; i < keyboardLength; i++) {
      if (i % 2 == 0) {
        inlineKeyboardButtons.add(rows.toArray(new InlineKeyboardButton[]{}));
        rows = new ArrayList<>();
      }

      rows.add(new InlineKeyboardButton(String.valueOf(i + 1)).callbackData(String.valueOf(i)));
    }

    inlineKeyboardButtons.add(rows.toArray(new InlineKeyboardButton[]{}));
    return new InlineKeyboardMarkup(inlineKeyboardButtons.toArray(new InlineKeyboardButton[][]{}));
  }

  private InlineKeyboardMarkup buildTopicChoiceKeyboard() {
    String[] allTopicsName = readRepository.getAllTopicNames();
    List<InlineKeyboardButton[]> inlineKeyboardButtons = new ArrayList<>();
    List<InlineKeyboardButton> rows = new ArrayList<>();
    for (int i = 0; i < allTopicsName.length; i++) {
      if (i % 2 == 0) {
        inlineKeyboardButtons.add(rows.toArray(new InlineKeyboardButton[]{}));
        rows = new ArrayList<>();
      }

      rows.add(new InlineKeyboardButton(allTopicsName[i]).callbackData("topicChoice:" + allTopicsName[i]));
    }

    inlineKeyboardButtons.add(rows.toArray(new InlineKeyboardButton[]{}));
    return new InlineKeyboardMarkup(inlineKeyboardButtons.toArray(new InlineKeyboardButton[][]{}));
  }

  private boolean isRegisterUser(Long chatId) {
    return users.containsKey(chatId);
  }

  private void clearLastMessageKeyboard(Long chatId) {
    Message message = users.get(chatId).getLastBotMessage();
    EditMessageText editMessage = new EditMessageText(chatId, message.messageId(), message.text());
    editMessage.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("").callbackData("deleted")));
    bot.execute(editMessage);
  }

  private void chooseQuiz(String callbackData, Long chatId) {
    UserInfo userInfo = users.get(chatId);
    String topicPrefix = "topicChoice:";
    String topicName = callbackData.substring(topicPrefix.length());
    userInfo.setCurrentTopicName(topicName);
    clearLastMessageKeyboard(chatId);
    userInfo.setChoiceQuiz(false);
    bot.execute(new SendMessage(chatId, String.format("Quiz: %s\n write /start_quiz or /choice for choice any quiz",
            topicName)));
  }

  private String getAnswerText(UserQuizSession userQuizSession, int userAnswerNum) {
    Question question = userQuizSession.getCurrentQuestion();
    int questionAnswerNum = question.getAnswer();
    String userAnswerText = question.getOptions()[userAnswerNum];
    String quizAnswerOption = question.getOptions()[questionAnswerNum];
    String quizAnswerDescription = question.getAnswerDescription();
    String answerMessageText;
    if (questionAnswerNum == userAnswerNum) {
      answerMessageText = String.format("✅ It's Right!\n\n<b>Answer:</b> %s\n\n%s",
              quizAnswerOption, quizAnswerDescription);
      userQuizSession.addRightCounter();
    } else {
      answerMessageText = String.format("❌ It's wrong!\n\n<b>Your answer:</b> %s\n<b>Right answer:</b> %s\n\n%s",
              userAnswerText, quizAnswerOption, quizAnswerDescription);
    }
    return answerMessageText;
  }

}











