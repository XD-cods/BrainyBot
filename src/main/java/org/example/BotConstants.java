package org.example;

public class BotConstants {

  public static final String START_BOT_COMMAND = "/start";
  public static final String CHOOSE_TOPIC_COMMAND = "/choice";
  public static final String START_QUIZ_COMMAND = "/start_quiz";
  public static final String CANCEL_COMMAND = "/cancel";
  public static final String CREATE_QUIZ_COMMAND = "/create";
  public static final String QUESTION_OPTIONS_CREATE_COMMAND = "/done";
  public static final String CREATE_QUIZ_END = "/create";
  public static final String CANCEL_CREATE_QUIZ_COMMAND = "/cancel_create";
  public static final String INPUT_NEXT_QUESTION_COMMAND = "/next";

  public static final String STARTING_MESSAGE = "\uD83D\uDE2E Hello! I'm bot for testing your knowledge! " +
      "\uD83E\uDD2F\n\n❓For choosing quiz input /choice❓\n❓For starting quiz /start_quiz❓";
  public static final String CREATE_VALIDATION_MESSAGE = "You are creating quiz, please finish" +
          " create quiz or input /cancel_create";

  private BotConstants() {
  }
}
