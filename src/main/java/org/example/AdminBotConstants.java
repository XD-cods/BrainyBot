package org.example;

public class AdminBotConstants {
  /*commands*/
  public static final String START_BOT_COMMAND = "/start";
  public static final String ADD_NEW_QUIZ_COMMAND = "/add";
  public static final String UPDATE_QUIZ_COMMAND = "/update";
  public static final String CLEAR_DB_COMMAND = "/clear_database";
  public static final String CANCEL_COMMAND = "/cancel";


  public static final String START_BOT_MESSAGE = "Hello admin here's a list of commands you can use:\n"
          + ADD_NEW_QUIZ_COMMAND + "\n"
          + UPDATE_QUIZ_COMMAND + "\n"
          + CLEAR_DB_COMMAND + "\n"
          + CANCEL_COMMAND + "\n";

  private AdminBotConstants() {
  }

}
