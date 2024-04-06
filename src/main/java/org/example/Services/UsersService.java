package org.example.Services;

import org.example.Repositories.UsersRepo;
import org.example.model.PermanentUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
  @Autowired
  private UsersRepo usersRepo;

  public PermanentUserInfo addNewUser(String userName, Long userId) {
    PermanentUserInfo permanentUserInfo = new PermanentUserInfo(userName, userId, false);
    usersRepo.insert(permanentUserInfo);
    return permanentUserInfo;
  }

  public PermanentUserInfo findPemanentUserInfo(String userName, Long userId) {
    PermanentUserInfo permanentUserInfo = usersRepo.findByUserName(userName);
    if (permanentUserInfo == null) {
      return addNewUser(userName, userId);
    }
    return permanentUserInfo;
  }
}
