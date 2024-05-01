package org.example.Repositories.Mongo;

import org.example.model.PermanentUserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends MongoRepository<PermanentUserInfo, String> {
  PermanentUserInfo findByUserName(String userName);
}
