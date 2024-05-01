package org.example.Services;

import org.example.model.PermanentUserInfo;
import org.example.model.TempUserInfo;
import org.example.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
  private final RedisTemplate<Long, UserInfo> redisTemplate;
  private final UsersService usersService;

  @Autowired
  public RedisService(RedisTemplate<Long, UserInfo> redisTemplate, UsersService usersService) {
    this.redisTemplate = redisTemplate;
    this.usersService = usersService;
  }

  public void persistUser(UserInfo user, Long userId) {
    if (user.getPermanentUserInfo().getIsAdmin()) {
      redisTemplate.opsForValue().set(userId, user);
      return;
    }
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
}
