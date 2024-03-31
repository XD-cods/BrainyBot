package org.example.Repositories;

import org.example.model.QuestionOption;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionOptionsRepository extends MongoRepository<QuestionOption,String> {
}
