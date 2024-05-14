package org.example.services;

import org.example.repositories.UserRepo;
import org.example.model.PermanentUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
  private final UserRepo userRepo;

  @Autowired
  public UsersService(UserRepo userRepo) {
    this.userRepo = userRepo;
  }

  public PermanentUserInfo addNewUser(String userName, Long userId) {
    PermanentUserInfo permanentUserInfo = new PermanentUserInfo(userName, userId, false);
    userRepo.insert(permanentUserInfo);
    return permanentUserInfo;
  }

}
