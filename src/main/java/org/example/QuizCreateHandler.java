package org.example;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.model.Question;
import org.example.model.QuestionOption;
import org.example.model.Quiz;
import org.example.model.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QuizCreateHandler {
  private final Map<Long, UserInfo> users;
  private final TelegramBot bot;
  private QuizBuilder quizBuilder;
  private final DAORepository daoRepository;

  public QuizCreateHandler(Map<Long, UserInfo> users, TelegramBot bot,
                           QuizBuilder quizBuilder, DAORepository daoRepository) {
    this.users = users;
    this.bot = bot;
    this.quizBuilder = quizBuilder;
    this.daoRepository = daoRepository;
  }

  void handleQuizCreation(Long userId, String messageText) {

    if (quizBuilder.getTopicName().isEmpty() && !messageText.startsWith("/")) {
      quizBuilder.setTopicName(messageText);
      bot.execute(new SendMessage(userId, "Please input here your question"));
      return;
    }

    List<Question> questionList = quizBuilder.getQuestionList();
    int questionListSize = questionList.size();

    if (messageText.startsWith("/")) {
      switch (messageText) {
        case BotConstants.CANCEL_CREATE_QUIZ_COMMAND -> {
          quizBuilder = new QuizBuilder();
          users.get(userId).setCreateMode(false);
        }
        case BotConstants.QUESTION_OPTIONS_CREATE_COMMAND -> {
          Question lastQuestion = questionList.get(questionListSize - 1);
          if (lastQuestion.getOptionList().size() < 2) {
            bot.execute(new SendMessage(userId, "Please add options to your question"));
            break;
          }
          bot.execute(new SendMessage(userId, "input answer description "
                  + BotConstants.CREATE_QUIZ_COMMAND + " for create your quiz"));
          Collections.shuffle(lastQuestion.getOptionList());
          quizBuilder.setInputOptions(false);
          quizBuilder.setInputAnswerDescription(true);
          break;
        }
        case BotConstants.CREATE_QUIZ_COMMAND -> {
          if (questionListSize == 0 ) {
            bot.execute(new SendMessage(userId, "Please add questions to your quiz"));
            break;
          }
          if(quizBuilder.isInputOptions()){
            bot.execute(new SendMessage(userId, "Please finish input option for question"));
            break;
          }
          Collections.shuffle(questionList);
          daoRepository.addNewQuiz(new Quiz(quizBuilder.getTopicName(), questionList));
        }
      }
      return;
    }

    if(quizBuilder.isInputAnswerDescription()){
      createAnswerDescription(userId, messageText, questionList, questionListSize);
      return;
    }

    if(quizBuilder.isInputOptions()) {
      createQuestionOption(userId, messageText, questionList, questionListSize);
    } else {
      createQuestion(userId, messageText, questionList);
    }
  }

  private void createQuestionOption(Long userId, String messageText, List<Question> questionList, int questionListSize) {
    List<QuestionOption> questionOptionList = questionList.get(questionListSize - 1).getOptionList();
    if (quizBuilder.isInputAnswerOptions()) {
      questionOptionList.add(new QuestionOption(true, messageText));
      bot.execute(new SendMessage(userId, "Write new options or write /done for finish input options"));
      quizBuilder.setInputAnswerOptions(false);
      return;
    }
    bot.execute(new SendMessage(userId, "Write new options or write /done for finish input options"));
    questionOptionList.add(new QuestionOption(false, messageText));
  }

  private void createQuestion(Long userId, String messageText, List<Question> questionList) {
    Question question = new Question();
    question.setQuestion(messageText);
    questionList.add(question);
    question.setOptionList(new ArrayList<>());
    bot.execute(new SendMessage(userId, "Write answer options"));
    quizBuilder.setInputAnswerOptions(true);
    quizBuilder.setInputOptions(true);
  }

  private void createAnswerDescription(Long userId, String messageText, List<Question> questionList, int questionListSize) {
    questionList.get(questionListSize - 1).setAnswerDescription(messageText);
    bot.execute(new SendMessage(userId, "input new question or input "
            + BotConstants.CREATE_QUIZ_COMMAND + " for create your quiz"));
    quizBuilder.setInputAnswerDescription(false);
  }
}
