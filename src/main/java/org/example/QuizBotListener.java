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
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.event.CacheEntryCreatedListener;
import org.cache2k.event.CacheEntryExpiredListener;
import org.example.model.Question;
import org.example.model.QuestionOption;
import org.example.model.QuizBotSession;
import org.example.model.QuizQuestions;
import org.example.model.UserQuizSession;
import org.example.services.QuizService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuizBotListener implements UpdatesListener {
  private static final Logger log = LogManager.getLogger(QuizBotListener.class);
  private final TelegramBot bot;
  private final QuizService quizService;
  private final Cache<Long, QuizBotSession> sessionCache;

  public QuizBotListener(TelegramBot bot, QuizService quizService) {
    this.bot = bot;
    this.quizService = quizService;
    this.sessionCache = Cache2kBuilder.of(Long.class, QuizBotSession.class).expireAfterWrite(120, TimeUnit.SECONDS).addListener((CacheEntryExpiredListener<Long, QuizBotSession>) (cache, entry) -> {
      clearLastMessageKeyboard(cache.get(entry.getKey()), entry.getKey());
      sendMessage(entry.getKey(), "You've been thinking too long, so you'll have to start over." + " Write /start or /choice.");
    }).addListener((CacheEntryCreatedListener<Long, QuizBotSession>) (cache, entry) -> sendMessage(entry.getKey(), UserBotConstants.STARTING_MESSAGE)).build();
  }

  private static String getQuizStatText(int quizAmount, int rightAnswerCounter) {
    return String.format("❓ <b>Question number:</b> %d" + "\n\n" + "✅ <b>Right answers:</b> %d\\%d" + "\n\n" + "Input " + UserBotConstants.START_QUIZ_COMMAND + " to start quiz or chose quiz another quiz " + UserBotConstants.CHOOSE_TOPIC_COMMAND, quizAmount, rightAnswerCounter, quizAmount);
  }

  private static String getCanceledQuizStatText(int questionCount, int rightAnswerCounter) {
    return String.format("❓<b>You canceled quiz</b>\n" + "\n" + "<b>The questions were:</b> %d\n\n" + "✅ <b>Right answers:</b> %d\\%d\n" + "\n" + "Input " + UserBotConstants.START_QUIZ_COMMAND + " to start quiz or chose quiz another quiz " + UserBotConstants.CHOOSE_TOPIC_COMMAND, questionCount, rightAnswerCounter, questionCount);
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
      QuizBotSession quizBotSession = sessionCache.computeIfAbsent(userId,
              () -> new QuizBotSession(QuizBotSessionMode.SESSION_CREATED));
      switch (quizBotSession.getBotSessionMode()) {
        case TOPIC_CHOICE -> {
          if (messageText.equals(UserBotConstants.CANCEL_COMMAND)) {
            quizBotSession.setBotSessionMode(QuizBotSessionMode.SESSION_CREATED);
            sendMessage(userId, UserBotConstants.STARTING_MESSAGE);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
          }
          choiceTopic(messageText, quizBotSession, userId);
          return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }
        case QUIZ -> {
          if (messageText.equals(UserBotConstants.CANCEL_COMMAND)) {
            clearLastMessageKeyboard(quizBotSession, userId);
            sendStatsCanceledQuiz(userId, quizBotSession);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
          }
          sendMessage(userId, "Please finish this quiz");
          return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }

        case QUESTION_COUNT_CHOICE -> {
          if (messageText.equals(UserBotConstants.CANCEL_COMMAND)) {
            quizBotSession.setBotSessionMode(QuizBotSessionMode.SESSION_CREATED);
            sendMessage(userId, UserBotConstants.STARTING_MESSAGE);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
          }
          setCountOfQuiz(messageText, userId, quizBotSession);
          return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }
      }

      switch (messageText) {
        case UserBotConstants.START_BOT_COMMAND -> {
          clearLastMessageKeyboard(quizBotSession, userId);
          sessionCache.remove(userId);
          sessionCache.put(userId, new QuizBotSession(QuizBotSessionMode.SESSION_CREATED));
          return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }
        case UserBotConstants.CHOOSE_TOPIC_COMMAND -> sendTopics(userId, quizBotSession);
        case UserBotConstants.START_QUIZ_COMMAND -> {
          String currentTopicName = quizBotSession.getCurrentTopicName();
          if (currentTopicName.isEmpty()) {
            sendMessage(userId, "Pleas choice topic " + UserBotConstants.CHOOSE_TOPIC_COMMAND);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
          }
          int countOfQuestion = quizBotSession.getCountOfQuestion();
          QuizQuestions quizQuestions = quizService.findRandomQuestionsByTopicName(currentTopicName, countOfQuestion);
          UserQuizSession userQuizSession = new UserQuizSession(quizQuestions);
          sendMessage(userId, "Quiz: " + currentTopicName);
          quizBotSession.setUserQuizSession(userQuizSession);
          sendQuestion(userId, quizBotSession);
          quizBotSession.setBotSessionMode(QuizBotSessionMode.QUIZ);
        }
        case UserBotConstants.CANCEL_COMMAND -> sendMessage(userId, "Nothing canceled");
        default -> sendMessage(userId, "Input a valid command");
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }

  private void setCountOfQuiz(String messageText, Long userId, QuizBotSession quizBotSession) {
    if (!messageText.matches("^[1-9][0-9]*$")) {
      sendMessage(userId, "Input a number or input " + UserBotConstants.CANCEL_COMMAND + " for cancel count of question choice");
      return;
    }
    int countOfQuestions = Integer.parseInt(messageText);
    if (countOfQuestions > 20 || countOfQuestions < 5) {
      sendMessage(userId, "Input valid count of question");
    }
    quizBotSession.setCountOfQuestion(countOfQuestions);
    sendMessage(userId, String.format("Quiz: %s\nQuestions: %d\nInput " + UserBotConstants.START_QUIZ_COMMAND + " or " + UserBotConstants.CHOOSE_TOPIC_COMMAND + " for choice any topic", quizBotSession.getCurrentTopicName(), countOfQuestions));
    quizBotSession.setBotSessionMode(QuizBotSessionMode.SESSION_CREATED);
  }

  private void sendMessage(Long userId, String s) {
    bot.execute(new SendMessage(userId, s));
  }

  private void sendTopics(Long userId, QuizBotSession quizBotSession) {
    List<String> allTopicsName = quizService.getTopics();
    if (allTopicsName.isEmpty()) {
      allTopicsName = quizService.getTopics();
      if (allTopicsName.isEmpty()) {
        sendMessage(userId, "Sorry nothing topics");
        return;
      }
    }
    StringBuilder choiceTopicText = new StringBuilder("Choose your topic! Input number of quiz");
    quizBotSession.setBotSessionMode(QuizBotSessionMode.TOPIC_CHOICE);
    for (int i = 0; i < allTopicsName.size(); i++) {
      int pagination = i + 1;
      choiceTopicText.append("\n").append(pagination).append(". ").append(allTopicsName.get(i));
    }
    SendMessage choiceTopic = new SendMessage(userId, choiceTopicText.toString());
    Message message = bot.execute(choiceTopic).message();
    quizBotSession.setLastKeyboardBotMessageId(message.messageId());
    quizBotSession.setLastKeyboardBotMessageText(message.text());
  }

  private void sendQuestion(Long userId, QuizBotSession quizBotSession) {
    UserQuizSession userQuizSession = quizBotSession.getUserQuizSession();
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
    quizBotSession.setLastKeyboardBotMessageText(message.text());
    quizBotSession.setLastKeyboardBotMessageId(message.messageId());
  }

  private void sendAnswer(CallbackQuery callbackQuery, Long userId, QuizBotSession quizBotSession) {
    String callbackData = callbackQuery.data();
    UserQuizSession userQuizSession = quizBotSession.getUserQuizSession();
    int userAnswerNum = Integer.parseInt(callbackData);
    String answerText = getAnswerText(userQuizSession, userAnswerNum);
    SendMessage answerMessage = new SendMessage(userId, answerText).parseMode(ParseMode.HTML);
    clearLastMessageKeyboard(quizBotSession, userId);
    bot.execute(answerMessage);
  }

  private void sendQuizStats(Long userId, QuizBotSession quizBotSession) {
    UserQuizSession userQuizSession = quizBotSession.getUserQuizSession();
    int quizAmount = userQuizSession.getQuestionAmount();
    int rightAnswerCounter = userQuizSession.getRightAnswerCounter();
    String statMessageText = getQuizStatText(quizAmount, rightAnswerCounter);
    SendMessage statMessage = new SendMessage(userId, statMessageText).parseMode(ParseMode.HTML);
    bot.execute(statMessage).message();
    quizBotSession.setUserQuizSession(null);
  }

  private void sendStatsCanceledQuiz(Long userId, QuizBotSession quizBotSession) {
    UserQuizSession userQuizSession = quizBotSession.getUserQuizSession();
    int questionCount = userQuizSession.getQuestionCounter();
    int rightAnswerCounter = userQuizSession.getRightAnswerCounter();
    String statMessageText = getCanceledQuizStatText(questionCount, rightAnswerCounter);
    SendMessage statMessage = new SendMessage(userId, statMessageText).parseMode(ParseMode.HTML);
    Message message = bot.execute(statMessage).message();
    quizBotSession.setLastKeyboardBotMessageId(message.messageId());
    quizBotSession.setLastKeyboardBotMessageText(message.text());
    quizBotSession.setUserQuizSession(null);
  }

  private int handleCallback(CallbackQuery callbackQuery) {
    Long userId = callbackQuery.from().id();
    QuizBotSession quizBotSession = sessionCache.computeIfAbsent(userId, () -> new QuizBotSession(QuizBotSessionMode.SESSION_CREATED));

    String callbackData = callbackQuery.data();
    if (callbackData == null) {
      return UpdatesListener.CONFIRMED_UPDATES_NONE;
    }
    clearLastMessageKeyboard(quizBotSession, userId);
    handlerQuizAnswer(callbackQuery, userId, quizBotSession);
    return UpdatesListener.CONFIRMED_UPDATES_ALL;
  }

  private void handlerQuizAnswer(CallbackQuery callbackQuery, Long userId, QuizBotSession quizBotSession) {
    UserQuizSession userQuizSession = quizBotSession.getUserQuizSession();
    if (userQuizSession == null) {
      return;
    }
    int totalQuestion = userQuizSession.getQuestionAmount();
    int currentQuestionIndex = userQuizSession.getCurrentQuestionIndex();
    //todo refactoring
    if (quizBotSession.getBotSessionMode().equals(QuizBotSessionMode.QUIZ)) {
      sendAnswer(callbackQuery, userId, quizBotSession);
    }

    if (currentQuestionIndex == totalQuestion) {
      sendQuizStats(userId, quizBotSession);
    }

    if (currentQuestionIndex < totalQuestion) {
      sendQuestion(userId, quizBotSession);
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

  private void choiceTopic(String messageText, QuizBotSession quizBotSession, Long userId) {
    if (!messageText.matches("^[1-9][0-9]*$")) {
      sendMessage(userId, "Input a number or input " + UserBotConstants.CANCEL_COMMAND + " for cancel topic choice");
      return;
    }
    int topicIndex = Integer.parseInt(messageText) - 1;
    List<String> allTopicName = quizService.getTopics();
    if (allTopicName.isEmpty()) {
      quizBotSession.setBotSessionMode(QuizBotSessionMode.SESSION_CREATED);
      sendMessage(userId, "Sorry nothing quiz, send " + UserBotConstants.CHOOSE_TOPIC_COMMAND + " for choice quiz");
      return;
    }
    if (topicIndex >= allTopicName.size() || topicIndex < 0) {
      sendMessage(userId, "Send me correct digital from 1 to " + allTopicName.size());
      sendTopics(userId, quizBotSession);
      return;
    }
    String currentTopicName = allTopicName.get(topicIndex);
    quizBotSession.setCurrentTopicName(currentTopicName);
    quizBotSession.setBotSessionMode(QuizBotSessionMode.QUESTION_COUNT_CHOICE);
    sendMessage(userId, "Input count to your quiz (at 5 to 20)");
  }

  private void clearLastMessageKeyboard(QuizBotSession quizBotSession, Long userId) {
    if (quizBotSession.getLastKeyboardBotMessageId() == 0) {
      return;
    }
    EditMessageText editMessage = new EditMessageText(userId, quizBotSession.getLastKeyboardBotMessageId(), quizBotSession.getLastKeyboardBotMessageText());
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
      userQuizSession.incRightCounter();
    } else {
      answerMessageText = String.format("❌ It's wrong!\n\n<b>Your answer:</b> %s\n<b>Right answer:</b> %s\n\n%s", userAnswerText, quizAnswerOption, quizAnswerDescription);
    }
    return answerMessageText;
  }

}