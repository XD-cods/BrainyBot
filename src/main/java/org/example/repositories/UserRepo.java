package org.example.repositories;

import org.example.model.PermanentUserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository<PermanentUserInfo, String> {
  PermanentUserInfo findByUserName(String userName); //todo by user id
}
