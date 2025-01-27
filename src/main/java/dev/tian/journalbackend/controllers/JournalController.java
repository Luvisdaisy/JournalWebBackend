package dev.tian.journalbackend.controllers;

import dev.tian.journalbackend.models.Comment;
import dev.tian.journalbackend.models.Journal;
import dev.tian.journalbackend.services.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling journal-related HTTP requests.
 */
@RestController
@RequestMapping("/api/journal")
public class JournalController
{
    private final JournalService journalService;

    @Autowired
    public JournalController(JournalService journalService)
    {
        this.journalService = journalService;
    }

    /**
     * Retrieves all journals with pagination and sorting by creation date.
     *
     * @param page Page number (default: 0).
     * @param size Number of journals per page (default: 5).
     *
     * @return A list of journals.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Journal>> getAllJournals(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDatetime")));
        Page<Journal> journalPage = journalService.getAllJournals(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(journalPage.getContent());
    }

    /**
     * Retrieves journals of friends for a given username, sorted by update date.
     *
     * @param page     Page number (default: 0).
     * @param size     Number of journals per page (default: 5).
     * @param username Username of the user whose friends' journals are to be retrieved.
     *
     * @return A list of friends' journals.
     */
    @GetMapping("/friends/{username}")
    public ResponseEntity<List<Journal>> getFriendsJournals(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, @PathVariable String username)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("updatedDatetime")));
        Page<Journal> journalPage = journalService.getFriendsJournals(pageable, username);
        return ResponseEntity.status(HttpStatus.OK).body(journalPage.getContent());
    }

    /**
     * Retrieves journals by a specific username, sorted by creation date.
     *
     * @param page     Page number (default: 0).
     * @param size     Number of journals per page (default: 5).
     * @param username Username of the journal creator.
     *
     * @return A list of journals.
     */
    @GetMapping("/{username}")
    public ResponseEntity<List<Journal>> getJournalsByUsername(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, @PathVariable String username)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDatetime")));
        Page<Journal> journalPage = journalService.getJournalsByUsername(pageable, username);
        return ResponseEntity.status(HttpStatus.OK).body(journalPage.getContent());
    }

    /**
     * Searches for journals containing specific keywords.
     *
     * @param keywords Keywords to search for in journal titles and content.
     * @param page     Page number (default: 0).
     * @param size     Number of journals per page (default: 5).
     *
     * @return A list of matching journals.
     */
    @GetMapping("/search")
    public List<Journal> getRelatedJournals(@RequestParam String keywords, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size)
    {
        return journalService.getRelatedJournals(keywords);
    }

    /**
     * Creates a new journal.
     *
     * @param journal The journal to be created.
     *
     * @return The created journal.
     */
    @PostMapping("/new")
    public ResponseEntity<Journal> newJournal(@RequestBody Journal journal)
    {
        Journal newJournal = journalService.newJournal(journal);
        return ResponseEntity.status(HttpStatus.CREATED).body(newJournal);
    }

    /**
     * Likes a journal.
     *
     * @param id       ID of the journal to like.
     * @param username Username of the user who liked the journal.
     *
     * @return The updated journal.
     */
    @PutMapping("/like")
    public ResponseEntity<Journal> likeJournal(@RequestParam String id, @RequestParam String username)
    {
        Journal likedJournal = journalService.likeJournal(id, username);
        if (likedJournal == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(likedJournal);
    }

    /**
     * Unlikes a journal.
     *
     * @param id       ID of the journal to unlike.
     * @param username Username of the user who unliked the journal.
     *
     * @return The updated journal.
     */
    @PutMapping("/unlike")
    public ResponseEntity<Journal> unlikeJournal(@RequestParam String id, @RequestParam String username)
    {
        Journal unlikedJournal = journalService.unlikeJournal(id, username);
        if (unlikedJournal == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(unlikedJournal);
    }

    /**
     * Adds a comment to a journal.
     *
     * @param comment The comment to be added.
     * @param id      ID of the journal to add the comment to.
     *
     * @return The added comment.
     */
    @PutMapping("/comment/{id}")
    public ResponseEntity<Comment> newComment(@RequestBody Comment comment, @PathVariable String id)
    {
        Comment newComment = journalService.addComment(id, comment);
        if (newComment == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(newComment);
    }

    /**
     * Updates a journal.
     *
     * @param journal The updated journal data.
     * @param id      ID of the journal to update.
     *
     * @return The updated journal.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Journal> updateJournal(@RequestBody Journal journal, @PathVariable String id)
    {
        Journal updatedJournal = journalService.updateJournal(journal, id);
        if (updatedJournal == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedJournal);
    }

    /**
     * Deletes a journal.
     *
     * @param id ID of the journal to delete.
     *
     * @return A response indicating success or failure.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJournal(@PathVariable String id)
    {
        if (!journalService.deleteJournal(id))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal not found");
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Journal deleted successfully");
    }
}