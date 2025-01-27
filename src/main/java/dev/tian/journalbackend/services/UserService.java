package dev.tian.journalbackend.services;

import dev.tian.journalbackend.models.User;
import dev.tian.journalbackend.models.UserRelationship;
import dev.tian.journalbackend.repositories.UserRelationshipRepository;
import dev.tian.journalbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService
{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRelationshipRepository userRelationshipRepository;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, UserRelationshipRepository userRelationshipRepository)
    {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRelationshipRepository = userRelationshipRepository;
    }

    /**
     * Find a user by username.
     *
     * @param username Username to search
     *
     * @return Optional containing the user, if found
     */
    public Optional<User> findUserByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    /**
     * Search users by display name with pagination.
     *
     * @param displayName Display name to search
     * @param page        Page number
     * @param size        Page size
     *
     * @return List of users matching the display name
     */
    public List<User> findUserByDisplayName(String displayName, int page, int size)
    {
        return userRepository.findByDisplayNameContaining(displayName, page, size);
    }

    /**
     * Add a new user to the database.
     *
     * @param user User details
     *
     * @return Saved user object
     */
    public User addUser(User user)
    {
        if(userRepository.existsByUsername(user.getUsername()))
        {
            throw new IllegalArgumentException("Username already exists");
        }
        user.setUsername(user.getUsername().toLowerCase());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setCreatedDatetime(LocalDateTime.now());
        user.setUpdatedDatetime(null);
        user.setDisplayName(user.getUsername());
        user.setGender("Other");
        user.setIsActivated(false);
        user.setIsDeleted(false);
        user.setAvatar("src/assets/user.svg");

        initUserRelationship(user.getUsername());
        return userRepository.save(user);
    }

    /**
     * Initialize user relationship metadata.
     *
     * @param username Username to initialize relationships for
     */
    private void initUserRelationship(String username)
    {
        UserRelationship userRelationship = new UserRelationship();
        userRelationship.setUsername(username);
        userRelationship.setFollowing(List.of());
        userRelationship.setFollowers(List.of());
        userRelationship.setBlocked(List.of());
        userRelationship.setFriends(List.of());
        userRelationshipRepository.save(userRelationship);
    }

    /**
     * Update an existing user's information.
     *
     * @param user     Updated user details
     * @param username Username to update
     *
     * @return Optional containing the updated user, if successful
     */
    public Optional<User> updateUser(User user, String username)
    {
        User existingUser = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 更新用户的字段
        existingUser.setDisplayName(user.getDisplayName());
        existingUser.setAvatar(user.getAvatar());
        existingUser.setEmail(user.getEmail());
        existingUser.setUpdatedDatetime(LocalDateTime.now());

        return Optional.of(userRepository.save(existingUser));
    }

    /**
     * Delete a user by username.
     *
     * @param username Username to delete
     *
     * @return true if user was deleted, false if not found
     */
    public boolean deleteUser(String username)
    {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent())
        {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }

    /**
     * Authenticate a user with their username and password.
     *
     * @param username Username
     * @param password Plaintext password
     */
    public void authenticate(String username, String password)
    {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        if (!bCryptPasswordEncoder.matches(password, user.getPassword()))
        {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
