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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Question;
import org.example.model.QuestionOption;
import org.example.model.TempUserInfo;
import org.example.model.UserQuizSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotUpdate implements UpdatesListener {

  private final TelegramBot bot;
  private final Map<Long, TempUserInfo> users = new HashMap<>();
  //todo refactoring daoRepository on MongoService
  private final DAORepository DAORepository;
  private final Logger logger = LogManager.getLogger();

  public BotUpdate(TelegramBot bot, DAORepository DAORepository) {
    this.bot = bot;
    this.DAORepository = DAORepository;
  }

  @Override
  public int process(List<Update> updates) throws NullPointerException {
    Update update = updates.get(updates.size() - 1);
    try {
      if (update.callbackQuery() != null) {
        return handleCallback(update.callbackQuery());
      } else if (update.message() == null) {
        return UpdatesListener.CONFIRMED_UPDATES_NONE;
      }
      Message message = update.message();
      Long userId = message.chat().id();
      String messageText = message.text();
      if (messageText.isEmpty()) {
        bot.execute(new SendMessage(userId, "input /start"));
        users.remove(userId);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
      }

      if (!isRegisterUser(userId) && !messageText.equals(UserBotConstants.START_BOT_COMMAND)) {
        bot.execute(new SendMessage(userId, "input /start"));
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
      }

      switch (messageText) {
        case UserBotConstants.START_BOT_COMMAND -> {
          bot.execute(new SendMessage(userId, UserBotConstants.STARTING_MESSAGE));
          users.remove(userId);
          TempUserInfo tempUserInfo = new TempUserInfo();
          users.put(userId, tempUserInfo);
        }
        case UserBotConstants.CHOOSE_TOPIC_COMMAND -> {
          TempUserInfo tempUserInfo = users.get(userId);
          UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
          if (userQuizSession != null) {
            bot.execute(new SendMessage(userId, "Please finish this quiz"));
            break;
          }
          if (tempUserInfo.isTopicChosen()) {
            bot.execute(new SendMessage(userId, "You are not chosen quiz"));
            break;
          }
          sendTopics(userId);
        }
        case UserBotConstants.START_QUIZ_COMMAND -> {
          TempUserInfo tempUserInfo = users.get(userId);
          UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
          if (userQuizSession != null) {
            bot.execute(new SendMessage(userId, "Please finish this quiz"));
            break;
          }
          String currentTopicName = tempUserInfo.getCurrentTopicName();
          if (currentTopicName == null) {
            bot.execute(new SendMessage(userId, "Topic is not chosen, please use /choice command to choose"));
            break;
          }
          if (tempUserInfo.isTopicChosen()) {
            bot.execute(new SendMessage(userId, "You are not chosen quiz"));
            break;
          }
          userQuizSession = new UserQuizSession(DAORepository.loadQuiz(currentTopicName));
          tempUserInfo.setUserQuizSession(userQuizSession);
          bot.execute(new SendMessage(userId, "Quiz: " + tempUserInfo.getCurrentTopicName()));
          sendQuestion(userId);
        }
        case UserBotConstants.CANCEL_COMMAND -> {
          TempUserInfo tempUserInfo = users.get(userId);
          UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
          if (userQuizSession == null) {
            bot.execute(new SendMessage(userId, "You aren't begin quiz"));
            break;
          }
          clearLastMessageKeyboard(users.get(userId).getLastkeyboardBotMessage(), userId);
          sendStatsCanceledQuiz(userId);
        }
      }

//      if(users.get(userId).isTopicChosen() && isDigital(messageText)){
//
//      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return UpdatesListener.CONFIRMED_UPDATES_ALL;

  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


  private boolean isDigital(String string){
    try{
      Integer.parseInt(string);
      return true;
    } catch (Exception e){
      return false;
    }
  }

  private void sendTopics(Long userId) {
    List<String> allTopicsName = DAORepository.getAllTopicNames();
    StringBuilder choiceTopicText = new StringBuilder("Choose your topic! Input number of quiz");
    SendMessage choiceTopic = new SendMessage(userId, choiceTopicText.toString());
    users.get(userId).setChoiceTopic(true);
    for (int i = 0; i < allTopicsName.size(); i++) {
      int pagination = i + 1;
      choiceTopicText.append("\n").append(pagination).append(". ").append(allTopicsName.get(i));
    }
    users.get(userId).setLastkeyboardBotMessage(bot.execute(choiceTopic).message());
  }

  private void sendQuestion(Long userId) {
    UserQuizSession userQuizSession = users.get(userId).getUserQuizSession();
    Question question = userQuizSession.getNextQuestion();
    List<QuestionOption> optionsList = question.getOptionList();
    Keyboard inlineKeyboardMarkup = buildInlineKeyboard(optionsList.size());
    StringBuilder questionTextMessage = new StringBuilder(String.format("❓ Question: %d\n%s\n",
            userQuizSession.getQuestionCounter(), question.getQuestion()));
    for (int i = 0; i < optionsList.size(); i++) {
      questionTextMessage.append(String.format("\n%d. %s", i + 1, optionsList.get(i).getOptionText()));
    }

    SendMessage questionMessage = new SendMessage(userId, questionTextMessage.toString());
    questionMessage.replyMarkup(inlineKeyboardMarkup);
    users.get(userId).setLastkeyboardBotMessage(bot.execute(questionMessage).message());
  }

  private void sendAnswer(CallbackQuery callbackQuery, Long userId) {
    clearLastMessageKeyboard(callbackQuery.message(), userId);
    int userAnswerNum = Integer.parseInt(callbackQuery.data());
    UserQuizSession userQuizSession = users.get(userId).getUserQuizSession();
    String answerText = getAnswerText(userQuizSession, userAnswerNum);
    SendMessage answerMessage = new SendMessage(userId, answerText).parseMode(ParseMode.HTML);
    bot.execute(answerMessage);
  }

  private void sendQuizStats(Long userId) {
    UserQuizSession userQuizSession = users.get(userId).getUserQuizSession();
    int quizAmount = userQuizSession.getQuestionAmount();
    int rightAnswerCounter = userQuizSession.getRightAnswerCounter();
    String statMessageText = getQuizStatText(quizAmount, rightAnswerCounter);
    userQuizSession.setQuizMode(false);
    SendMessage statMessage = new SendMessage(userId, statMessageText).parseMode(ParseMode.HTML);
    bot.execute(statMessage).message();
    users.get(userId).setUserQuizSession(null);
  }

  private static String getQuizStatText(int quizAmount, int rightAnswerCounter) {
    return String.format("""
                    ❓ <b>Question number:</b> %d

                    ✅ <b>Right answers:</b> %d\\%d

                    Input /start_quiz to reset bot or chose quiz another quiz /choice""",
            quizAmount, rightAnswerCounter, quizAmount);
  }

  private void sendStatsCanceledQuiz(Long userId) {
    UserQuizSession userQuizSession = users.get(userId).getUserQuizSession();
    int questionCount = userQuizSession.getQuestionCounter();
    int rightAnswerCounter = userQuizSession.getRightAnswerCounter();
    String statMessageText = getCanceledQuizStatText(questionCount, rightAnswerCounter);
    userQuizSession.setQuizMode(false);
    SendMessage statMessage = new SendMessage(userId, statMessageText).parseMode(ParseMode.HTML);
    users.get(userId).setLastkeyboardBotMessage(bot.execute(statMessage).message());
    users.get(userId).setUserQuizSession(null);
  }

  private static String getCanceledQuizStatText(int questionCount, int rightAnswerCounter) {
    return String.format("""
                    ❓<b>You canceled quiz</b>

                    <b>The questions were:</b> %d

                    ✅ <b>Right answers:</b> %d\\%d

                    Input /start_quiz to reset bot or chose quiz another quiz /choice""",
            questionCount, rightAnswerCounter, questionCount);
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
    if (callbackData.startsWith("topicChoice:") && users.get(userId).isTopicChosen()) {
      chooseQuiz(callbackQuery, userId);
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    clearLastMessageKeyboard(callbackQuery.message(), userId);
    handlerQuizAnswer(callbackQuery, userId);
    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }

  private void handlerQuizAnswer(CallbackQuery callbackQuery, Long userId) {
    TempUserInfo userInfo = users.get(userId);
    UserQuizSession userQuizSession = userInfo.getUserQuizSession();
    if (userQuizSession.isQuizMode()) {
      sendAnswer(callbackQuery, userId);
    }

    if (userQuizSession.getQuestionCounter() == userQuizSession.getQuestionAmount()) {
      sendQuizStats(userId);
    }

    if (userQuizSession.isNextQuestionAvailable()) {
      sendQuestion(userId);
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

  private boolean isRegisterUser(Long userId) {
    return users.containsKey(userId);
  }

  private void clearLastMessageKeyboard(Message message, Long userId) {
    EditMessageText editMessage = new EditMessageText(userId, message.messageId(), message.text());
    editMessage.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("").callbackData("deleted")));
    bot.execute(editMessage);
  }

  private void chooseQuiz(CallbackQuery callbackQuery, Long userId) {
    TempUserInfo tempUserInfo = users.get(userId);
    String callbackData = callbackQuery.data();
    String topicPrefix = "topicChoice:";
    String topicName = callbackData.substring(topicPrefix.length());
    tempUserInfo.setCurrentTopicName(topicName);
    clearLastMessageKeyboard(callbackQuery.message(), userId);
    tempUserInfo.setChoiceTopic(false);
    bot.execute(new SendMessage(userId, String.format("Quiz: %s\n input /start_quiz or /choice for choice any quiz",
            topicName)));
  }

  private String getAnswerText(UserQuizSession userQuizSession, int userAnswerNum) {
    Question question = userQuizSession.getCurrentQuestion();
    List<QuestionOption> optionsList = question.getOptionList();
    String quizAnswerOption = null;
    int questionAnswerNum = 0;
    for (int i = 0; i < optionsList.size(); i++) {
      QuestionOption questionOption = optionsList.get(i);
      if (questionOption.isAnswer()) {
        questionAnswerNum = i;
        quizAnswerOption = questionOption.getOptionText();
        break;
      }
    }
    String userAnswerText = question.getAnswerDescription();
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