package org.example.services;

import org.example.model.PermanentUserInfo;
import org.example.model.QuizBotSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Deprecated(forRemoval = true)
@Service
public class RedisService {
  private final RedisTemplate<Long, UserInfo> redisTemplate;
  private final UsersService userService;

  @Autowired
  public RedisService(RedisTemplate<Long, UserInfo> redisTemplate, UsersService userService) {
    this.redisTemplate = redisTemplate;
    this.userService = userService;
  }


  public UserInfo findUser(String userName, Long userId) {
    //todo storage only temp user info in redis
    return Optional.ofNullable(redisTemplate.opsForValue().get(userId))
            .orElseGet(() -> {
              PermanentUserInfo permanentUserInfo = userService.findPemanentUserInfo(userName, userId);
              UserInfo userInfo = new UserInfo(permanentUserInfo, new QuizBotSession());
              persistUser(userInfo, userId);
              return userInfo;
            });
  }

  public void updateUserInfo(Long userId, QuizBotSession quizBotSession) {
    //todo storage only temp user info in redis
    UserInfo userInfo = redisTemplate.opsForValue().get(userId);
    if (userInfo != null) {
      userInfo.setTempUserInfo(quizBotSession);
      persistUser(userInfo, userId);
    }
  }

  private void persistUser(UserInfo user, Long userId) {
    redisTemplate.opsForValue().set(userId, user, 120, TimeUnit.SECONDS);
  }
}
