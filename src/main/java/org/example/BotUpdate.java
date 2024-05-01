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
import org.example.Services.QuizService;
import org.example.Services.RedisService;
import org.example.model.Question;
import org.example.model.QuestionOption;
import org.example.model.Quiz;
import org.example.model.TempUserInfo;
import org.example.model.UserQuizSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BotUpdate implements UpdatesListener {

  private static final Logger log = LogManager.getLogger(BotUpdate.class);
  private final TelegramBot bot;
  private final QuizService quizService;
  private final RedisService redisService;

  public BotUpdate(TelegramBot bot, QuizService quizService, RedisService redisService) {
    this.bot = bot;
    this.quizService = quizService;
    this.redisService = redisService;
  }

  private static String getQuizStatText(int quizAmount, int rightAnswerCounter) {
    return String.format("❓ <b>Question number:</b> %d" + "\n\n"
                         + "✅ <b>Right answers:</b> %d\\%d" + "\n\n"
                         + "Input " + UserBotConstants.START_QUIZ_COMMAND
                         + " to start quiz or chose quiz another quiz "
                         + UserBotConstants.CHOOSE_TOPIC_COMMAND, quizAmount, rightAnswerCounter, quizAmount);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private static String getCanceledQuizStatText(int questionCount, int rightAnswerCounter) {
    return String.format("❓<b>You canceled quiz</b>\n" + "\n"
                         + "<b>The questions were:</b> %d\n\n"
                         + "✅ <b>Right answers:</b> %d\\%d\n" + "\n"
                         + "Input " + UserBotConstants.START_QUIZ_COMMAND
                         + " to start quiz or chose quiz another quiz "
                         + UserBotConstants.CHOOSE_TOPIC_COMMAND, questionCount, rightAnswerCounter, questionCount);
  }

  @Override
  public int process(List<Update> updates) throws NullPointerException {
    try {
      Update update = updates.get(updates.size() - 1);
      if (update.callbackQuery() != null) {
        return handleCallback(update.callbackQuery());
      } else if (update.message() == null) {
        return UpdatesListener.CONFIRMED_UPDATES_NONE;
      }

      Message message = update.message();
      Long userId = message.chat().id();
      String messageText = message.text();
      TempUserInfo tempUserInfo = redisService.findUser(message.chat().username(), userId).getTempUserInfo();
      if (messageText.isEmpty()) {
        sendMessage(userId, "input " + UserBotConstants.START_BOT_COMMAND);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
      }

      switch (messageText) {
        case UserBotConstants.START_BOT_COMMAND -> {
          redisService.updateUserInfo(userId, new TempUserInfo());
          sendMessage(userId, UserBotConstants.STARTING_MESSAGE);
        }
        case UserBotConstants.CHOOSE_TOPIC_COMMAND -> {
          UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
          if (userQuizSession != null) {
            sendMessage(userId, "Please finish this quiz");
            break;
          }
          sendTopics(userId, tempUserInfo);
        }
        case UserBotConstants.START_QUIZ_COMMAND -> {
          UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
          if (userQuizSession != null) {
            sendMessage(userId, "Please finish this quiz");
            break;
          }
          String currentTopicName = tempUserInfo.getCurrentTopicName();
          if (currentTopicName == null) {
            sendMessage(userId, "Topic is not chosen, please use " + UserBotConstants.CHOOSE_TOPIC_COMMAND + " command to choose");
            break;
          }
          if (tempUserInfo.isChoiceTopic()) {
            sendMessage(userId, "You are not chosen quiz");
            break;
          }

          userQuizSession = new UserQuizSession(getGeneratedQuiz(currentTopicName, tempUserInfo));
          tempUserInfo.setUserQuizSession(userQuizSession);
          redisService.updateUserInfo(userId, tempUserInfo);
          sendMessage(userId, "Quiz: " + tempUserInfo.getCurrentTopicName());
          sendQuestion(userId, tempUserInfo);
        }
        case UserBotConstants.CANCEL_COMMAND -> {
          UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
          if (userQuizSession == null) {
            sendMessage(userId, "You aren't begin quiz");
            break;
          }
          clearLastMessageKeyboard(tempUserInfo, userId);
          sendStatsCanceledQuiz(userId, tempUserInfo);
        }
      }

      if (messageText.matches("[0-9]+$")) {
        if (tempUserInfo.isChoiceCountOfQuestion()) {
          setCountOfQuiz(messageText, userId, tempUserInfo);
        }
        if (tempUserInfo.isChoiceTopic()) {
          choiceTopic(messageText, tempUserInfo, userId);
        }
      }
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }

  private void setCountOfQuiz(String messageText, Long userId, TempUserInfo tempUserInfo) {
    int countOfQuestions = Integer.parseInt(messageText);
    if (countOfQuestions > 20 || countOfQuestions < 5) {
      sendMessage(userId, "Input valid count of question");
    }
    tempUserInfo.setCountOfQuestion(countOfQuestions);
    sendMessage(userId, String.format("Quiz: %s\nQuestions:%d\nInput " + UserBotConstants.START_QUIZ_COMMAND + " or " + UserBotConstants.CHOOSE_TOPIC_COMMAND + " for choice any topic", tempUserInfo.getCurrentTopicName(), countOfQuestions));
    tempUserInfo.setChoiceCountOfQuestion(false);
    redisService.updateUserInfo(userId, tempUserInfo);
  }

  private Quiz getGeneratedQuiz(String currentTopicName, TempUserInfo tempUserInfo) {
    Quiz quiz = quizService.findByTopicName(currentTopicName);
    List<Question> questionList = quiz.getQuestionList();
    int countOfQuestion = tempUserInfo.getCountOfQuestion();
    if (questionList.size() <= countOfQuestion) {
      return quiz;
    }
    Quiz generatedQuiz = new Quiz();
    List<Question> generatedQuestionList = new ArrayList<>();
    Set<Integer> uniqueNumbers = getUniqueNums(questionList.size() - 1, countOfQuestion);
    for (Integer i : uniqueNumbers) {
      generatedQuestionList.add(questionList.get(i));
    }
    generatedQuiz.setQuestionList(generatedQuestionList);
    generatedQuiz.setTopicName(quiz.getTopicName());
    return generatedQuiz;
  }

  private Set<Integer> getUniqueNums(int max, int count) {
    Set<Integer> uniqueNums = new HashSet<>();
    Random random = new Random();
    while (count > uniqueNums.size()) {
      uniqueNums.add(random.nextInt(max));
    }
    return uniqueNums;
  }

  private void sendMessage(Long userId, String s) {
    bot.execute(new SendMessage(userId, s));
  }

  private void sendTopics(Long userId, TempUserInfo tempUserInfo) {
    List<String> allTopicsName = quizService.readTopicsFromFile();
    if (allTopicsName.isEmpty()) {
      quizService.updateTopicsFile();
      allTopicsName = quizService.readTopicsFromFile();
      if (allTopicsName.isEmpty()) {
        sendMessage(userId, "Sorry nothing topics");
        return;
      }
    }
    StringBuilder choiceTopicText = new StringBuilder("Choose your topic! Input number of quiz");
    tempUserInfo.setChoiceTopic(true);
    for (int i = 0; i < allTopicsName.size(); i++) {
      int pagination = i + 1;
      choiceTopicText.append("\n").append(pagination).append(". ").append(allTopicsName.get(i));
    }
    SendMessage choiceTopic = new SendMessage(userId, choiceTopicText.toString());
    Message message = bot.execute(choiceTopic).message();
    tempUserInfo.setLastKeyboardBotMessageId(message.messageId());
    tempUserInfo.setLastKeyboardBotMessageText(message.text());
    redisService.updateUserInfo(userId, tempUserInfo);
  }

  private void sendQuestion(Long userId, TempUserInfo tempUserInfo) {
    UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
    Question question = userQuizSession.getNextQuestion();
    List<QuestionOption> optionsList = question.getOptionList();
    Collections.shuffle(optionsList);
    Keyboard inlineKeyboardMarkup = buildInlineKeyboard(optionsList.size());
    StringBuilder questionTextMessage = new StringBuilder(String.format("❓ Question: %d\n%s\n", userQuizSession.getQuestionCounter(), question.getQuestion()));
    for (int i = 0; i < optionsList.size(); i++) {
      questionTextMessage.append(String.format("\n%d. %s", i + 1, optionsList.get(i).getOptionText()));
    }

    SendMessage questionMessage = new SendMessage(userId, questionTextMessage.toString());
    questionMessage.replyMarkup(inlineKeyboardMarkup);
    Message message = bot.execute(questionMessage).message();
    tempUserInfo.setLastKeyboardBotMessageText(message.text());
    tempUserInfo.setLastKeyboardBotMessageId(message.messageId());
    redisService.updateUserInfo(userId, tempUserInfo);
  }

  private void sendAnswer(CallbackQuery callbackQuery, Long userId, TempUserInfo tempUserInfo) {
    String callbackData = callbackQuery.data();
    UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
    int userAnswerNum = Integer.parseInt(callbackData);
    String answerText = getAnswerText(userQuizSession, userAnswerNum);
    SendMessage answerMessage = new SendMessage(userId, answerText).parseMode(ParseMode.HTML);
    clearLastMessageKeyboard(tempUserInfo, userId);
    bot.execute(answerMessage);
  }

  private void sendQuizStats(Long userId, TempUserInfo tempUserInfo) {
    UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
    int quizAmount = userQuizSession.getQuestionAmount();
    int rightAnswerCounter = userQuizSession.getRightAnswerCounter();
    String statMessageText = getQuizStatText(quizAmount, rightAnswerCounter);
    SendMessage statMessage = new SendMessage(userId, statMessageText).parseMode(ParseMode.HTML);
    userQuizSession.setQuizMode(false);
    bot.execute(statMessage).message();
    tempUserInfo.setUserQuizSession(null);
    redisService.updateUserInfo(userId, tempUserInfo);
  }

  private void sendStatsCanceledQuiz(Long userId, TempUserInfo tempUserInfo) {
    UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
    int questionCount = userQuizSession.getQuestionCounter();
    int rightAnswerCounter = userQuizSession.getRightAnswerCounter();
    String statMessageText = getCanceledQuizStatText(questionCount, rightAnswerCounter);
    userQuizSession.setQuizMode(false);
    SendMessage statMessage = new SendMessage(userId, statMessageText).parseMode(ParseMode.HTML);
    Message message = bot.execute(statMessage).message();
    tempUserInfo.setLastKeyboardBotMessageId(message.messageId());
    tempUserInfo.setLastKeyboardBotMessageText(message.text());
    tempUserInfo.setUserQuizSession(null);
    redisService.updateUserInfo(userId, tempUserInfo);
  }

  private int handleCallback(CallbackQuery callbackQuery) {
    Long userId = callbackQuery.from().id();
    TempUserInfo tempUserInfo = redisService.findUser(callbackQuery.from().username(), userId).getTempUserInfo();
    String callbackData = callbackQuery.data();
    if (callbackData == null) {
      return UpdatesListener.CONFIRMED_UPDATES_NONE;
    }
    clearLastMessageKeyboard(tempUserInfo, userId);
    handlerQuizAnswer(callbackQuery, userId, tempUserInfo);
    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }

  private void handlerQuizAnswer(CallbackQuery callbackQuery, Long userId, TempUserInfo tempUserInfo) {
    UserQuizSession userQuizSession = tempUserInfo.getUserQuizSession();
    if (userQuizSession.isQuizMode()) {
      sendAnswer(callbackQuery, userId, tempUserInfo);
    }

    if (userQuizSession.getQuestionCounter() == userQuizSession.getQuestionAmount()) {
      sendQuizStats(userId, tempUserInfo);
    }

    if (userQuizSession.isNextQuestionAvailable()) {
      sendQuestion(userId, tempUserInfo);
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

  private void choiceTopic(String messageText, TempUserInfo tempUserInfo, Long userId) {
    int topicIndex = Integer.parseInt(messageText) - 1;
    List<String> allTopicName = quizService.readTopicsFromFile();
    if (allTopicName.isEmpty()) {
      tempUserInfo.setChoiceTopic(false);
      quizService.updateTopicsFile();
      sendMessage(userId, "Sorry nothing quiz, send " + UserBotConstants.CHOOSE_TOPIC_COMMAND + " for choice quiz");
      return;
    }
    if (topicIndex >= allTopicName.size()) {
      sendMessage(userId, "You input over large digital, send me correct digital");
      sendTopics(userId, tempUserInfo);
      return;
    } else if (topicIndex <= 0) {
      sendMessage(userId, "You input so small digital, send me correct digital");
      sendTopics(userId, tempUserInfo);
      return;
    }
    String currentTopicName = allTopicName.get(topicIndex);
    tempUserInfo.setCurrentTopicName(currentTopicName);
    tempUserInfo.setChoiceTopic(false);
    tempUserInfo.setCurrentQuiz(quizService.findByTopicName(currentTopicName));
    tempUserInfo.setChoiceCountOfQuestion(true);
    redisService.updateUserInfo(userId, tempUserInfo);
    sendMessage(userId, "Input count to your quiz (at 5 to 20)");
  }

  private void clearLastMessageKeyboard(TempUserInfo tempUserInfo, Long userId) {
    EditMessageText editMessage = new EditMessageText(userId, tempUserInfo.getLastKeyboardBotMessageId(), tempUserInfo.getLastKeyboardBotMessageText());
    editMessage.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton("").callbackData("deleted")));
    bot.execute(editMessage);
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
      answerMessageText = String.format("✅ It's Right!\n\n<b>Answer:</b> %s\n\n%s", quizAnswerOption, quizAnswerDescription);
      userQuizSession.addRightCounter();
    } else {
      answerMessageText = String.format("❌ It's wrong!\n\n<b>Your answer:</b> %s\n<b>Right answer:</b> %s\n\n%s", userAnswerText, quizAnswerOption, quizAnswerDescription);
    }
    return answerMessageText;
  }

}