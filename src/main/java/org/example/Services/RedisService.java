package org.example.Services;

import com.pengrad.telegrambot.model.User;
import org.example.Repositories.Redis.RedisRepo;
import org.example.model.PermanentUserInfo;
import org.example.model.TempUserInfo;
import org.example.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisService {
  private RedisTemplate<Long, UserInfo> redisTemplate;
  private RedisRepo redisRepo;
  private UsersService usersService;

  @Autowired
  public RedisService(RedisTemplate<Long, UserInfo> redisTemplate, RedisRepo redisRepo, UsersService usersService) {
    this.redisTemplate = redisTemplate;
    this.redisRepo = redisRepo;
    this.usersService = usersService;
  }

  public Long countOfUser(){
    return redisRepo.count();
  }

  public void addNewUser(UserInfo user){
    redisTemplate.opsForValue().set(user.getPermanentUserInfo().getUserId(), user);
  }

  public UserInfo findUser(String userName, Long userId){
    Optional<UserInfo> user = redisRepo.findById(userId);
    if (user.isEmpty()) {
      PermanentUserInfo permanentUserInfo = usersService.findPemanentUserInfo(userName, userId);
      UserInfo userInfo = new UserInfo(permanentUserInfo, new TempUserInfo());
      addNewUser(userInfo);
      user = Optional.of(userInfo);
    }
    return user.get();
  }
}
