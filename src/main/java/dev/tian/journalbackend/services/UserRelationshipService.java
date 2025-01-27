package dev.tian.journalbackend.services;

import dev.tian.journalbackend.models.SimpleUser;
import dev.tian.journalbackend.models.User;
import dev.tian.journalbackend.models.UserRelationship;
import dev.tian.journalbackend.repositories.UserRelationshipRepository;
import dev.tian.journalbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class for managing user relationships.
 */
@Service
public class UserRelationshipService
{
    private final UserRelationshipRepository userRelationshipRepository;
    private final UserRepository userRepository;

    /**
     * Constructor for UserRelationshipService.
     *
     * @param userRelationshipRepository the user relationship repository
     * @param userRepository             the user repository
     */
    @Autowired
    public UserRelationshipService(UserRelationshipRepository userRelationshipRepository, UserRepository userRepository)
    {
        this.userRelationshipRepository = userRelationshipRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get the user relationship details by username.
     *
     * @param username the username of the user
     *
     * @return the user relationship details
     *
     * @throws UsernameNotFoundException if the user relationship is not found
     */
    public UserRelationship getUserRelationship(String username)
    {
        UserRelationship userRelationship = userRelationshipRepository.findByUsername(username);
        if (userRelationship == null)
        {
            throw new UsernameNotFoundException("User relationship not found for: " + username);
        }
        return userRelationship;
    }

    /**
     * Add a new following relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to follow
     *
     * @throws RuntimeException if an error occurs while following
     */
    public void newFollowing(String username, String targetUsername)
    {
        try
        {
            UserRelationship userRelationship = userRelationshipRepository.findByUsername(username);
            if (userRelationship == null)
            {
                throw new UsernameNotFoundException("User relationship not found for: " + username);
            }

            User targetUser = userRepository.findByUsername(targetUsername).orElseThrow(() -> new UsernameNotFoundException("Target user not found"));
            SimpleUser simpleUser = new SimpleUser(targetUser.getUsername(), targetUser.getDisplayName(), targetUser.getAvatar());
            userRelationship.getFollowing().add(simpleUser);

            userRelationshipRepository.save(userRelationship);
        } catch (UsernameNotFoundException e)
        {
            throw new RuntimeException("User not found: " + e.getMessage());
        } catch (Exception e)
        {
            throw new RuntimeException("Error while following: " + e.getMessage());
        }
    }

    /**
     * Remove a following relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to unfollow
     *
     * @throws RuntimeException if an error occurs while unfollowing
     */
    public void unFollowing(String username, String targetUsername)
    {
        try
        {
            UserRelationship userRelationship = userRelationshipRepository.findByUsername(username);
            if (userRelationship == null)
            {
                throw new UsernameNotFoundException("User relationship not found for: " + username);
            }

            userRelationship.getFollowing().removeIf(following -> following.getUsername().equals(targetUsername));
            userRelationshipRepository.save(userRelationship);
        } catch (Exception e)
        {
            throw new RuntimeException("Error while unfollowing: " + e.getMessage());
        }
    }

    /**
     * Add a new follower relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to follow
     *
     * @throws RuntimeException if an error occurs while following
     */
    public void newFollower(String username, String targetUsername)
    {
        try
        {
            UserRelationship userRelationship = userRelationshipRepository.findByUsername(username);
            if (userRelationship == null)
            {
                throw new UsernameNotFoundException("User relationship not found for: " + username);
            }

            User targetUser = userRepository.findByUsername(targetUsername).orElseThrow(() -> new UsernameNotFoundException("Target user not found"));
            SimpleUser simpleUser = new SimpleUser(targetUser.getUsername(), targetUser.getDisplayName(), targetUser.getAvatar());
            userRelationship.getFollowers().add(simpleUser);

            userRelationshipRepository.save(userRelationship);
        } catch (UsernameNotFoundException e)
        {
            throw new RuntimeException("User not found: " + e.getMessage());
        } catch (Exception e)
        {
            throw new RuntimeException("Error while following: " + e.getMessage());
        }
    }

    /**
     * Remove a follower relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to unfollow
     *
     * @throws RuntimeException if an error occurs while unfollowing
     */
    public void unFollower(String username, String targetUsername)
    {
        try
        {
            UserRelationship userRelationship = userRelationshipRepository.findByUsername(username);
            if (userRelationship == null)
            {
                throw new UsernameNotFoundException("User relationship not found for: " + username);
            }

            userRelationship.getFollowers().removeIf(follower -> follower.getUsername().equals(targetUsername));
            userRelationshipRepository.save(userRelationship);
        } catch (Exception e)
        {
            throw new RuntimeException("Error while unfollowing: " + e.getMessage());
        }
    }

    /**
     * Add a new friend relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to add as a friend
     *
     * @throws RuntimeException if an error occurs while adding a friend
     */
    public void newFriend(String username, String targetUsername)
    {
        try
        {
            UserRelationship userRelationship = userRelationshipRepository.findByUsername(username);
            if (userRelationship == null)
            {
                throw new UsernameNotFoundException("User relationship not found for: " + username);
            }

            User targetUser = userRepository.findByUsername(targetUsername).orElseThrow(() -> new UsernameNotFoundException("Target user not found"));
            SimpleUser simpleUser = new SimpleUser(targetUser.getUsername(), targetUser.getDisplayName(), targetUser.getAvatar());
            userRelationship.getFriends().add(simpleUser);

            userRelationshipRepository.save(userRelationship);
        } catch (UsernameNotFoundException e)
        {
            throw new RuntimeException("User not found: " + e.getMessage());
        } catch (Exception e)
        {
            throw new RuntimeException("Error while following: " + e.getMessage());
        }
    }

    /**
     * Remove a friend relationship.
     *
     * @param username       the username of the user
     * @param targetUsername the username of the target user to remove as a friend
     *
     * @throws RuntimeException if an error occurs while removing a friend
     */
    public void unFriend(String username, String targetUsername)
    {
        try
        {
            UserRelationship userRelationship = userRelationshipRepository.findByUsername(username);
            if (userRelationship == null)
            {
                throw new UsernameNotFoundException("User relationship not found for: " + username);
            }

            userRelationship.getFriends().removeIf(friend -> friend.getUsername().equals(targetUsername));
            userRelationshipRepository.save(userRelationship);
        } catch (Exception e)
        {
            throw new RuntimeException("Error while unfollowing: " + e.getMessage());
        }
    }
}
