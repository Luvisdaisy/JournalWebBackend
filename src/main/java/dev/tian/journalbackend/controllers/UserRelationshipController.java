package dev.tian.journalbackend.controllers;

import dev.tian.journalbackend.models.UserRelationship;
import dev.tian.journalbackend.services.UserRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user relationships.
 */
@RestController
@RequestMapping("/api/relationship")
public class UserRelationshipController
{
    private final UserRelationshipService userRelationshipService;

    /**
     * Constructor for UserRelationshipController.
     *
     * @param userRelationshipService the user relationship service
     */
    @Autowired
    public UserRelationshipController(UserRelationshipService userRelationshipService)
    {
        this.userRelationshipService = userRelationshipService;
    }

    /**
     * Get the user relationship details by username.
     *
     * @param username the username of the user
     * @param detail   the detail level (friends, following, followers)
     *
     * @return the user relationship details
     */
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserRelationship(@PathVariable String username, @RequestParam(required = false) String detail)
    {
        try
        {
            UserRelationship userRelationship = userRelationshipService.getUserRelationship(username);
            if (userRelationship == null)
            {
                return ResponseEntity.notFound().build();
            }

            if ("friends".equals(detail))
            {
                return ResponseEntity.ok(userRelationship.getFriends());
            }
            else if ("following".equals(detail))
            {
                return ResponseEntity.ok(userRelationship.getFollowing());
            }
            else if ("followers".equals(detail))
            {
                return ResponseEntity.ok(userRelationship.getFollowers());
            }

            return ResponseEntity.ok(userRelationship);
        } catch (Exception e)
        {
            return ResponseEntity.status(500).body("An error occurred while fetching the user relationship: " + e.getMessage());
        }
    }

    /**
     * Add a new following relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to follow
     *
     * @return the response entity with the status of the operation
     */
    @PutMapping("/following")
    public ResponseEntity<String> newFollowing(@RequestParam String username, @RequestParam String targetUsername)
    {
        try
        {
            userRelationshipService.newFollowing(username, targetUsername);
            return ResponseEntity.accepted().body("Following Success");
        } catch (Exception e)
        {
            return ResponseEntity.status(400).body("Failed to follow: " + e.getMessage());
        }
    }

    /**
     * Remove a following relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to unfollow
     *
     * @return the response entity with the status of the operation
     */
    @DeleteMapping("/following")
    public ResponseEntity<String> delFollowing(@RequestParam String username, @RequestParam String targetUsername)
    {
        try
        {
            userRelationshipService.unFollowing(username, targetUsername);
            return ResponseEntity.accepted().body("Unfollow Success");
        } catch (Exception e)
        {
            return ResponseEntity.status(400).body("Failed to unfollow: " + e.getMessage());
        }
    }

    /**
     * Add a new follower relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to follow
     *
     * @return the response entity with the status of the operation
     */
    @PutMapping("/follower")
    public ResponseEntity<?> newFollower(@RequestParam String username, @RequestParam String targetUsername)
    {
        try
        {
            userRelationshipService.newFollower(username, targetUsername);
            return ResponseEntity.accepted().body("Follow Success");
        } catch (Exception e)
        {
            return ResponseEntity.status(400).body("Failed to follow: " + e.getMessage());
        }
    }

    /**
     * Remove a follower relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to unfollow
     *
     * @return the response entity with the status of the operation
     */
    @DeleteMapping("/follower")
    public ResponseEntity<?> delFollower(@RequestParam String username, @RequestParam String targetUsername)
    {
        try
        {
            userRelationshipService.unFollower(username, targetUsername);
            return ResponseEntity.accepted().body("Unfollow Success");
        } catch (Exception e)
        {
            return ResponseEntity.status(400).body("Failed to unfollow: " + e.getMessage());
        }
    }

    /**
     * Add a new friend relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to add as a friend
     *
     * @return the response entity with the status of the operation
     */
    @PutMapping("/friend")
    public ResponseEntity<?> newFriend(@RequestParam String username, @RequestParam String targetUsername)
    {
        try
        {
            userRelationshipService.newFriend(username, targetUsername);
            return ResponseEntity.accepted().body("Add Friend Success");
        } catch (Exception e)
        {
            return ResponseEntity.status(400).body("Failed to add friend: " + e.getMessage());
        }
    }

    /**
     * Remove a friend relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to remove as a friend
     *
     * @return the response entity with the status of the operation
     */
    @DeleteMapping("/friend")
    public ResponseEntity<?> delFriend(@RequestParam String username, @RequestParam String targetUsername)
    {
        try
        {
            userRelationshipService.unFriend(username, targetUsername);
            return ResponseEntity.accepted().body("Delete Friend Success");
        } catch (Exception e)
        {
            return ResponseEntity.status(400).body("Failed to delete friend: " + e.getMessage());
        }
    }
}