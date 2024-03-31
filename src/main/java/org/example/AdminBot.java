package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.mongodb.client.MongoClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import org.example.Configs.MongoConfig;
import org.example.Services.Service;
import org.example.model.Quiz;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoJsonSchemaCreator;
import org.springframework.data.mongodb.core.convert.JsonSchemaMapper;
import org.springframework.data.mongodb.core.convert.MongoJsonSchemaMapper;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdminBot implements UpdatesListener {
  private TelegramBot bot;
  private Service service;

  public AdminBot(TelegramBot bot, Service service) {
    this.bot = bot;
    this.service = service;
  }

  @Override
  public int process(List<Update> updates) {
    Update update = updates.get(updates.size() - 1);
    Message message;
    if (update.message() != null) {
      message = update.message();
    } else {
      return UpdatesListener.CONFIRMED_UPDATES_NONE;
    }
    givenValidInput_whenValidating_thenValid();
    if (message.document() != null) {
      Document document = update.message().document();
      System.out.println(getDownloadFileContent(document));
    }


    try {

      if (update.message() == null) {
        return UpdatesListener.CONFIRMED_UPDATES_NONE;
      }

      Long userId = message.chat().id();
      String messageText = message.text();

      switch (messageText) {
      }

    } finally {
      return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
  }


  private String getDownloadFileContent(Document document) {
    if (document != null && "application/json".equals(document.mimeType())) {
      String fileId = document.fileId();
      GetFile getFile = new GetFile(fileId);
      File file = bot.execute(getFile).file();
      if (file != null) {
        String filePath = file.filePath();
        String fileUrl = "https://api.telegram.org/file/bot" + bot.getToken() + "/" + filePath;
        String content;
        try {
          content = new String(bot.getFileContent(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        return content;
      } else {
        System.err.println("No file found for document ID: " + document.fileId());
      }
    }
    return "";
  }

  public void givenValidInput_whenValidating_thenValid() {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);
    JsonSchema jsonSchema;
    try {
      jsonSchema = jsonSchemaGenerator.generateSchema(Quiz.class);
      ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
      System.out.println(objectWriter.writeValueAsString(jsonSchema));
    } catch (JsonMappingException e) {
      throw new RuntimeException(e);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
