package org.example.Repositories.Redis;

import org.example.model.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepo extends CrudRepository<UserInfo, Long> {

}
