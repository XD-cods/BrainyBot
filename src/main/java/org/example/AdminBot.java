package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Services.QuizService;
import org.example.Services.RedisService;
import org.example.model.PermanentUserInfo;
import org.example.model.Quiz;
import org.example.model.TempUserInfo;
import org.example.model.UserInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class AdminBot implements UpdatesListener {
  private final TelegramBot bot;
  private final QuizService quizService;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Logger logger = LogManager.getLogger("adminBot");
  private final RedisService redisService;

  public AdminBot(TelegramBot bot, QuizService quizService, RedisService redisService) {
    this.bot = bot;
    this.quizService = quizService;
    this.redisService = redisService;
  }

  private static boolean isStartMessage(String messageText) {
    return messageText.equals(UserBotConstants.START_BOT_COMMAND);
  }

  private static boolean isDocument(Message message) {
    return message.document() != null;
  }

  @Override
  public int process(List<Update> updates) {
    Update update = updates.get(updates.size() - 1);
    try {
      if (update.message() == null) {
        return UpdatesListener.CONFIRMED_UPDATES_NONE;
      }

      Message message = update.message();
      Long userId = message.chat().id();
      String messageText = message.text();
      UserInfo userInfo = redisService.findUser(message.chat().username(), userId);
      TempUserInfo tempUserInfo = userInfo.getTempUserInfo();

      if (!isAdmin(userInfo.getPermanentUserInfo())) {
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
      } else if (isAdmin(userInfo.getPermanentUserInfo()) && isStartMessage(messageText)) {
        sendMessage(userId, AdminBotConstants.START_BOT_MESSAGE);
      }

      if (isDocument(message)) {
        DocumentHandler(userId, message, tempUserInfo);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
      }
      if (tempUserInfo.isUpdateChoiceTopic() && messageText.matches("[0-9]+$")) {
        sendFile(messageText, userId, tempUserInfo);
      }

      switch (messageText) {
        case AdminBotConstants.CLEAR_DB_COMMAND -> {
          if (tempUserInfo.isAddQuizMode()) {
            sendMessage(userId, "Input /add for finish add files or input /cancel");
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
          }
          sendMessage(userId, "Database cleared");
          quizService.deleteAllQuiz();
        }
        case AdminBotConstants.ADD_NEW_QUIZ_COMMAND -> {
          if (!tempUserInfo.isAddQuizMode()) {
            sendMessage(userId, "Input your .json files and input /add" + " for finish add files or input /cancel");
          }
          if (tempUserInfo.isAddQuizMode()) {
            sendMessage(userId, "You are canceled add quiz");
          }
          tempUserInfo.setAddQuizMode(!tempUserInfo.isAddQuizMode());
          redisService.updateUserInfo(userId, tempUserInfo);
        }
        case AdminBotConstants.UPDATE_QUIZ_COMMAND -> {
          if (tempUserInfo.isAddQuizMode()) {
            sendMessage(userId, "Input /add for finish add files or input /cancel");
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
          }
          if (tempUserInfo.isUpdateChoiceTopic() || tempUserInfo.isUpdateInputFIle()) {
            sendMessage(userId, "Please finish update quiz or input /cancel");
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
          }
          sendMessage(userId, "Input number of quiz");
          tempUserInfo.setUpdateChoiceTopic(true);
          redisService.updateUserInfo(userId, tempUserInfo);
          sendAllQuiz(userId);
        }
        case AdminBotConstants.CANCEL_COMMAND -> {
          sendMessage(userId, "Commands is canceled");
          redisService.updateUserInfo(userId, new TempUserInfo());
        }
      }

    } catch (Exception e) {
      logger.error(e.getMessage());
    } finally {
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
  }

  private void sendMessage(Long userId, String s) {
    bot.execute(new SendMessage(userId, s));
  }

  private void sendFile(String messageText, Long userId, TempUserInfo tempUserInfo) throws IOException {
    int topicIndex = Integer.parseInt(messageText) - 1;
    List<String> allTopicsName = quizService.readTopicsFromFile();
    if (allTopicsName.isEmpty()) {
      quizService.updateTopicsFile();
      allTopicsName = quizService.readTopicsFromFile();
      if (allTopicsName.isEmpty()) {
        sendMessage(userId, "Nothing topic pls add questions");
        return;
      }
    }
    String topicName = allTopicsName.get(topicIndex);
    Quiz quiz = quizService.findByTopicName(topicName);
    if (quiz == null) {
      quizService.updateTopicsFile();
      allTopicsName = quizService.readTopicsFromFile();
      quiz = quizService.findByTopicName(allTopicsName.get(topicIndex));
      if (quiz == null) {
        sendMessage(userId, "Not topic with that name");
        return;
      }
    }
    java.io.File tempFile = Files.createTempFile(topicName, ".json").toFile();

    try (FileWriter fileWriter = new FileWriter(tempFile)) {
      fileWriter.write(objectMapper.writeValueAsString(quiz));
    }
    sendMessage(userId, "Topic is " + topicName);
    bot.execute(new SendDocument(userId, tempFile));
    tempUserInfo.setUpdateChoiceTopic(false);
    tempUserInfo.setUpdateInputFIle(true);
    redisService.updateUserInfo(userId, tempUserInfo);
    tempFile.delete();
  }

  private void DocumentHandler(Long userId, Message message, TempUserInfo tempUserInfo) {
    Quiz quiz = getQuizFromFile(userId, message.document());
    if (quiz == null) {
      return;
    }
    if (tempUserInfo.isAddQuizMode()) {
      quizService.insertNewQuiz(quiz);
    }
    if (tempUserInfo.isUpdateInputFIle()) {
      quizService.updateQuizByTopicName(quiz.getTopicName(), quiz);
      tempUserInfo.setUpdateInputFIle(false);
      redisService.updateUserInfo(userId, tempUserInfo);
    }
  }

  private void sendAllQuiz(Long userId) {
    List<String> allTopicsName = quizService.readTopicsFromFile();
    if (allTopicsName.isEmpty()) {
      quizService.updateTopicsFile();
      allTopicsName = quizService.readTopicsFromFile();
      if (allTopicsName == null) {
        sendMessage(userId, "Sorry not a questions");
        return;
      }
    }
    StringBuilder choiceTopicText = new StringBuilder("Choose your topic! Input number of quiz");
    for (int i = 0; i < allTopicsName.size(); i++) {
      int pagination = i + 1;
      choiceTopicText.append("\n").append(pagination).append(". ").append(allTopicsName.get(i));
    }
    SendMessage topics = new SendMessage(userId, choiceTopicText.toString());
    bot.execute(topics);
  }

  private boolean isAdmin(PermanentUserInfo permanentUserInfo) {
    return permanentUserInfo.getIsAdmin();
  }

  private Quiz getQuizFromFile(Long userId, Document document) {
    if (document != null && "application/json".equals(document.mimeType())) {
      String fileId = document.fileId();
      GetFile getFile = new GetFile(fileId);
      File file = bot.execute(getFile).file();
      if (file != null) {
        String content;
        try {
          content = new String(bot.getFileContent(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        try {
          return objectMapper.readValue(content, Quiz.class);
        } catch (JsonProcessingException e) {
          sendMessage(userId, "Please send valid json " + e.getMessage());
          throw new RuntimeException(e);
        }
      } else {
        logger.error("No file found for document ID in admin bot: {}", document.fileId());
        throw new RuntimeException("No file found in adminBot");
      }
    }
    sendMessage(userId, "Please send .json file");
    return new Quiz();
  }
}
