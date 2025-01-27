package dev.tian.journalbackend.services;

import dev.tian.journalbackend.models.Comment;
import dev.tian.journalbackend.models.Journal;
import dev.tian.journalbackend.models.SimpleUser;
import dev.tian.journalbackend.repositories.JournalRepository;
import dev.tian.journalbackend.repositories.UserRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for handling journal-related business logic.
 */
@Service
public class JournalService
{
    private final JournalRepository journalRepository;
    private final UserRelationshipRepository userRelationshipRepository;

    @Autowired
    public JournalService(JournalRepository journalRepository, UserRelationshipRepository userRelationshipRepository)
    {
        this.journalRepository = journalRepository;
        this.userRelationshipRepository = userRelationshipRepository;
    }

    /**
     * Retrieves all journals with pagination.
     *
     * @param pageable Pagination and sorting information.
     *
     * @return A page of journals.
     */
    public Page<Journal> getAllJournals(Pageable pageable)
    {
        return journalRepository.findAll(pageable);
    }

    /**
     * Retrieves journals by a specific username.
     *
     * @param pageable Pagination and sorting information.
     * @param username Username of the journal creator.
     *
     * @return A page of journals.
     */
    public Page<Journal> getJournalsByUsername(Pageable pageable, String username)
    {
        return journalRepository.findAllByUsername(pageable, username);
    }

    /**
     * Searches for journals containing specific keywords.
     *
     * @param keywords Keywords to search for.
     *
     * @return A list of matching journals.
     */
    public List<Journal> getRelatedJournals(String keywords)
    {
        return journalRepository.findByTitleContainingOrContentContaining(keywords, keywords);
    }

    /**
     * Creates a new journal.
     *
     * @param journal The journal to be created.
     *
     * @return The created journal.
     */
    public Journal newJournal(Journal journal)
    {
        journal.setLikes(List.of());
        journal.setComments(List.of());
        journal.setIsDeleted(false);
        journal.setCreatedDatetime(LocalDateTime.now());
        journal.setUpdatedDatetime(LocalDateTime.now());
        return journalRepository.save(journal);
    }

    /**
     * Updates a journal.
     *
     * @param journal The updated journal data.
     * @param id      ID of the journal to update.
     *
     * @return The updated journal, or null if not found.
     */
    public Journal updateJournal(Journal journal, String id)
    {
        Optional<Journal> existingJournal = journalRepository.findById(id);
        if (existingJournal.isEmpty())
        {
            return null;
        }
        Journal updatedJournal = existingJournal.get();
        if (journal.getTitle() != null)
        {
            updatedJournal.setTitle(journal.getTitle());
        }
        if (journal.getContent() != null)
        {
            updatedJournal.setContent(journal.getContent());
        }
        updatedJournal.setUpdatedDatetime(LocalDateTime.now());
        return journalRepository.save(updatedJournal);
    }

    /**
     * Deletes a journal by marking it as deleted.
     *
     * @param id ID of the journal to delete.
     *
     * @return True if the journal was found and deleted, false otherwise.
     */
    public boolean deleteJournal(String id)
    {
        Optional<Journal> journal = journalRepository.findById(id);
        if (journal.isEmpty())
        {
            return false;
        }
        journal.get().setIsDeleted(true);
        journalRepository.save(journal.get());
        return true;
    }

    /**
     * Likes a journal.
     *
     * @param id       ID of the journal to like.
     * @param username Username of the user who liked the journal.
     *
     * @return The updated journal, or null if not found.
     */
    public Journal likeJournal(String id, String username)
    {
        Optional<Journal> journal = journalRepository.findById(id);
        if (journal.isPresent())
        {
            journal.get().getLikes().add(username);
            return journalRepository.save(journal.get());
        }
        return null;
    }

    /**
     * Unlikes a journal.
     *
     * @param id       ID of the journal to unlike.
     * @param username Username of the user who unliked the journal.
     *
     * @return The updated journal, or null if not found.
     */
    public Journal unlikeJournal(String id, String username)
    {
        Optional<Journal> journal = journalRepository.findById(id);
        if (journal.isPresent())
        {
            journal.get().getLikes().remove(username);
            return journalRepository.save(journal.get());
        }
        return null;
    }

    /**
     * Adds a comment to a journal.
     *
     * @param id      ID of the journal to add the comment to.
     * @param comment The comment to be added.
     *
     * @return The added comment, or null if the journal was not found.
     */
    public Comment addComment(String id, Comment comment)
    {
        Optional<Journal> journal = journalRepository.findById(id);
        if (journal.isPresent())
        {
            Comment newComment = new Comment(comment.getSimpleUser(), comment.getContent());
            journal.get().getComments().add(newComment);
            journalRepository.save(journal.get());
            return newComment;
        }
        return null;
    }

    /**
     * Retrieves journals of friends for a given username.
     *
     * @param pageable Pagination and sorting information.
     * @param username Username of the user whose friends' journals are to be retrieved.
     *
     * @return A page of friends' journals.
     */
    public Page<Journal> getFriendsJournals(Pageable pageable, String username)
    {
        List<SimpleUser> friends = userRelationshipRepository.findByUsername(username).getFriends();
        List<String> friendUsernames = friends.stream().map(SimpleUser::getUsername).toList();
        return journalRepository.findAllByUsernameIn(pageable, friendUsernames);
    }
}