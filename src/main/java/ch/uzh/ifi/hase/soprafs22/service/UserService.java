package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Date;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user.
 */

@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
      // creates User. Also checks if user exists.
    Date date = new Date();
    newUser.setCreation_date(date);
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);

    checkIfUserExists(newUser);

    newUser = userRepository.save(newUser);
    userRepository.flush();
    return newUser;
  }

  private void checkIfUserExists(User userToBeCreated) {
      // This is a helper method. It checks uniqueness of username.
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
    }
  }

  public User loginCredentials(User user) {
      // This method check if username and password provided by user is correct.
      // Throws exception in case of discrepancies.
      // If username, password correct, returns user information.
      String username = user.getUsername();
      String password = user.getPassword();
      User userByUsername = userRepository.findByUsername(username);

      String uniqueErrorMessage = "%s username not found. Please register!";
      if (userByUsername == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(uniqueErrorMessage, username));
      }

      String savedPassword = userByUsername.getPassword();

      String passwordErrorMessage = "Password incorrect! Try again!";
      if (!password.equals(savedPassword)) {
          throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, String.format(passwordErrorMessage));
      }
      userByUsername.setStatus(UserStatus.ONLINE);
      return userByUsername;
  }

  public User getUserbyUserID(Long id) {
      // Input: id
      // Returns: User information of given user id
      // Throws: Throws NOT FOUND exception in case given user id not found
      User userById = userRepository.findByid(id);

      String uniqueErrorMessage = "User with user id %s not found!";
      if (userById == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(uniqueErrorMessage, id));
      }
      return userById;
  }

  public User editUserbyUserID(User user) {
      // Input: user information to be edited
      // Functionality: Edit the user information
      // Return: Edited user information
      // Throws: NOT FOUND and CONFLICT exceptions
      Long userid = user.getId();
      String username = user.getUsername();
      Date birthday = user.getBirthday();

      User userbyID = userRepository.findByid(userid);

      String notFoundErrorMessage = "User with user id %s not found!";
      if (userbyID == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(notFoundErrorMessage, userid));
      }

      String uniqueErrorMessage = "Username already exist";
      if (username.equals(userbyID.getUsername())) {
          throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(uniqueErrorMessage));
      }

      if (username != null) {
          userbyID.setUsername(username);
      }
      if (birthday != null) {
          userbyID.setBirthday(birthday);
      }
      return userbyID;
  }

  public User logoutUserbyUserID(Long userid) {
      // Input: user id
      // Function: Change online status to offline
      // Return: Edited user information
      User userbyID = userRepository.findByid(userid);
      userbyID.setStatus(UserStatus.OFFLINE);
      return userbyID;
  }
}
