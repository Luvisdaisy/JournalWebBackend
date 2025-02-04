package dev.tian.journalbackend.repositories;

import dev.tian.journalbackend.models.Journal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JournalRepository extends MongoRepository<Journal, String>
{


//    Page<Journal> findAllByUsername(Pageable pageable, String username);

    Page<Journal> findByAuthorUsername(Pageable pageable, String username);

    Page<Journal> findAllByAuthor_UsernameIn(Pageable pageable, List<String> friendUsernames);

    List<Journal> findByTitleContainingOrContentContaining(String keywords, String keywords1);
}
