package dev.tian.journalbackend.repositories;

import dev.tian.journalbackend.models.UserRelationship;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRelationshipRepository extends MongoRepository<UserRelationship, String>
{
    UserRelationship findByUsername(String username);
}
