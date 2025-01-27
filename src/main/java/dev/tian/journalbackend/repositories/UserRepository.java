package dev.tian.journalbackend.repositories;

import dev.tian.journalbackend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, Long>
{
    Optional<User> findByUsername(String username);

    List<User> findByDisplayNameContaining(String displayName, int page, int size);

    boolean existsByUsername(String username);
}
