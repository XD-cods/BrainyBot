package org.example.repositories;

import org.bson.types.ObjectId;
import org.example.model.PermanentUserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends MongoRepository<PermanentUserInfo, String> {
}
