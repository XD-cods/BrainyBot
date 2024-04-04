package org.example.Services;

import org.example.Repositories.UsersRepo;
import org.example.model.PermanentUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
  @Autowired
  private UsersRepo usersRepo;

  public void addNewUser(PermanentUserInfo permanentUserInfo){
    usersRepo.insert(permanentUserInfo);
  }

  public PermanentUserInfo findByUserName(String userName){
    PermanentUserInfo permanentUserInfo = usersRepo.findByUserName(userName);
    if(permanentUserInfo == null){
      permanentUserInfo = new PermanentUserInfo(userName,false);
      addNewUser(permanentUserInfo);
    }
    return permanentUserInfo;
  }
}
