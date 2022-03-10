package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {
    private final UserService userService;
    UserController(UserService userService) {
    this.userService = userService;
    }

    // get api to get all users
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
          userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    // post api to register
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    // post api for login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO loginUser(@RequestBody LoginUserPostDTO loginUserPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertLoginUserPostDTOtoEntity(loginUserPostDTO);

        // check login credentials and if correct, provide entire user data
        User userData = userService.loginCredentials(userInput);
        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userData);
    }

    // get api for get full user information by userid
    @GetMapping(value = "/users/{id}")
    @ResponseBody
    public FullUserGetDTO getUserbyUserID(@PathVariable("id") long id) {
      User userData = userService.getUserbyUserID(id);
      return DTOMapper.INSTANCE.convertEntityToFullUserGetDTO(userData);
    }

    // put api for editing user
    @PutMapping(value = "/users/{id}")
    @ResponseBody
    public FullUserGetDTO editUser(@RequestBody EditUserPutDTO editUserPutDTO, @PathVariable("id") Long id) {
        User editUser = DTOMapper.INSTANCE.convertEditUserPutDTOtoEntity(editUserPutDTO);
        editUser.setId(id);
        User editedUser = userService.editUserbyUserID(editUser);
        return DTOMapper.INSTANCE.convertEntityToFullUserGetDTO(editedUser);
  }
    // put api for logout userstatus update
    @PutMapping(value = "/logout/{id}")
    @ResponseBody
    public FullUserGetDTO logoutUser(@PathVariable("id") Long id) {
        User loggedUser = userService.logoutUserbyUserID(id);
        return DTOMapper.INSTANCE.convertEntityToFullUserGetDTO(loggedUser);
    }
}
