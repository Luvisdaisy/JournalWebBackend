package dev.tian.journalbackend.controllers;

import dev.tian.journalbackend.models.LoginRequest;
import dev.tian.journalbackend.models.SimpleUser;
import dev.tian.journalbackend.models.User;
import dev.tian.journalbackend.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/user")
public class UserController
{
    private final UserService userService;

    /**
     * Constructor for UserController.
     *
     * @param userService the user service
     */
    @Autowired
    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    /**
     * Get a user by username.
     *
     * @param username the username of the user
     * @param details  whether to show detailed information
     *
     * @return the user details
     */
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(
            @PathVariable String username, @RequestParam(required = false, defaultValue = "false") boolean details
    )
    {
        return userService.findUserByUsername(username)
                .map(user -> {
                    if (details)
                    {
                        Map<String, String> userDetail = new HashMap<>();
                        userDetail.put("username", user.getUsername());
                        userDetail.put("displayName", user.getDisplayName());
                        userDetail.put("email", user.getEmail());
                        userDetail.put("avatar", user.getAvatar());
                        userDetail.put("gender", user.getGender());

                        // 获取创建日期到现在的天数
                        long daysSinceCreated = ChronoUnit.DAYS.between(user.getCreatedDatetime(), LocalDateTime.now());
                        userDetail.put("createdDays", String.valueOf(daysSinceCreated));

                        return ResponseEntity.ok(userDetail);
                    }

                    else
                    {
                        SimpleUser simpleUserDTO = new SimpleUser(user.getUsername(),
                                                                  user.getDisplayName(),
                                                                  user.getAvatar()
                        );
                        return ResponseEntity.ok(simpleUserDTO);
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(String.format("User with username '%s' not found.", username)));
    }

    /**
     * Find users by name.
     *
     * @param name the name to search for
     * @param page the page number for pagination
     * @param size the page size for pagination
     *
     * @return the list of users
     */
    @GetMapping("/search/{name}")
    public ResponseEntity<?> findUser(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    )
    {

        if (name == null || name.trim().isEmpty())
        {
            return ResponseEntity.badRequest().body("The 'name' parameter cannot be null or empty.");
        }

        try
        {
            if (name.startsWith("@"))
            {
                Optional<User> user = userService.findUserByUsername(name.substring(1));
                if (user.isPresent())
                {
                    SimpleUser simpleUserDTO = new SimpleUser(user.get().getUsername(),
                                                              user.get().getDisplayName(),
                                                              user.get().getAvatar()
                    );
                    return ResponseEntity.ok(simpleUserDTO);
                }
                else
                {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(String.format("User with username '%s' not found.", name));
                }
            }

            List<User> users = userService.findUserByDisplayName(name, page, size);
            if (users.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(String.format("No users found with display name '%s'.", name));
            }

            List<SimpleUser> userDTOs = users.stream()
                    .map(user -> new SimpleUser(user.getUsername(), user.getDisplayName(), user.getAvatar()))
                    .toList();

            return ResponseEntity.ok(userDTOs);

        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }


    /**
     * Register a new user.
     *
     * @param user the user to register
     *
     * @return the registered user
     */
    @PostMapping("/register")
    public ResponseEntity<User> newUser(@RequestBody User user)
    {
        User newUser = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    /**
     * Update an existing user.
     *
     * @param user     the user details to update
     * @param username the username of the user to update
     *
     * @return the updated user
     */
    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user, @PathVariable String username)
    {
        if (!username.equals(user.getUsername()))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The username in the request body does not match the path variable.");
        }

        Optional<User> updatedUser = userService.updateUser(user, username);

        return updatedUser.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * Delete a user by username.
     *
     * @param username the username of the user to delete
     *
     * @return the response entity
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username)
    {
        try
        {
            boolean isDeleted = userService.deleteUser(username);
            if (isDeleted)
            {
                return ResponseEntity.noContent().build(); // Return 204 No Content
            }
            else
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(String.format("User with username '%s' not found.", username));
            }
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while trying to delete the user.");
        }
    }

    /**
     * Authenticate a user and create a session.
     *
     * @param loginRequest the login request containing username and password
     * @param session      the HTTP session
     *
     * @return the response entity with login status and user details
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpSession session)
    {
        try
        {
            userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            User user = userService.findUserByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            session.setAttribute("user", loginRequest.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("user", Map.of("username",
                                        user.getUsername(),
                                        "displayName",
                                        user.getDisplayName(),
                                        "avatar",
                                        user.getAvatar()
            ));

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException | UsernameNotFoundException e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Invalid username or password"));
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status",
                                 "error",
                                 "message",
                                 "An unexpected error occurred. Please try again later."
                    ));
        }
    }
}