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

    /*
    Get all users
    */
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

    /*
    Register: Post API to create new user
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public FullUserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User createdUser = userService.createUser(userInput);
        return DTOMapper.INSTANCE.convertEntityToFullUserGetDTO(createdUser);
    }

    /*
    Login: Post API to login user
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO loginUser(@RequestBody LoginUserPostDTO loginUserPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertLoginUserPostDTOtoEntity(loginUserPostDTO);
        User userData = userService.loginCredentials(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userData);
    }

    /*
    Retrieve user profile from userid
     */
    @GetMapping(value = "/users/{id}")
    @ResponseBody
    public FullUserGetDTO getUserbyUserID(@PathVariable("id") long id) {
      User userData = userService.getUserbyUserID(id);
      return DTOMapper.INSTANCE.convertEntityToFullUserGetDTO(userData);
    }

    /*
    Edit/Update user profile
     */
    @PutMapping(value = "/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void editUser(@RequestBody EditUserPutDTO editUserPutDTO, @PathVariable("id") Long id) {
        User editUser = DTOMapper.INSTANCE.convertEditUserPutDTOtoEntity(editUserPutDTO);
        editUser.setId(id);
        User edited_user = userService.editUserbyUserID(editUser);
  }

    /*
    Logout: Change status of profile
     */
    @PutMapping(value = "/logout/{id}")
    @ResponseBody
    public FullUserGetDTO logoutUser(@PathVariable("id") Long id) {
        User loggedUser = userService.logoutUserbyUserID(id);
        return DTOMapper.INSTANCE.convertEntityToFullUserGetDTO(loggedUser);
    }
}
