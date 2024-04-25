package org.example.Services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.example.model.PermanentUserInfo;
import org.example.model.TempUserInfo;
import org.example.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
  private RedisTemplate<Long, UserInfo> redisTemplate;
  private UsersService usersService;

  @Autowired
  public RedisService(RedisTemplate<Long, UserInfo> redisTemplate, UsersService usersService) {
    this.redisTemplate = redisTemplate;
    this.usersService = usersService;
  }


  public void persistUser(UserInfo user, Long userId) {
    redisTemplate.opsForValue().set(userId, user, 5, TimeUnit.SECONDS);
  }

  public UserInfo findUser(String userName, Long userId) {
    UserInfo user = redisTemplate.opsForValue().get(userId);
    if (user == null) {
      PermanentUserInfo permanentUserInfo = usersService.findPemanentUserInfo(userName, userId);
      UserInfo userInfo = new UserInfo(permanentUserInfo, new TempUserInfo());
      persistUser(userInfo, userId);
      user = userInfo;
    }
    return user;
  }

  public void updateUserInfo(Long userId, TempUserInfo tempUserInfo) {
    UserInfo userInfo = redisTemplate.opsForValue().get(userId);
    if (userInfo != null) {
      userInfo.setTempUserInfo(tempUserInfo);
      persistUser(userInfo, userId);
    }
  }

  public void sendNotification(String userName) {
    PermanentUserInfo permanentUserInfo = usersService.findPermanentUserInfo(userName);
    TelegramBot telegramBot = new TelegramBot("6683005363:AAFHknGfItPK9EeiiwQmeHMb5t5M_lgh-LM");
    telegramBot.execute(new SendMessage(permanentUserInfo.getUserId(), "You are expired"));
  }
}
