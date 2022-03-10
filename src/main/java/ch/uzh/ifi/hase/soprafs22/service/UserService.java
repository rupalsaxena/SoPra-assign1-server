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
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
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
    Date date = new Date();
    newUser.setCreation_date(date);
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);

    checkIfUserExists(newUser);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    }
  }
  public User loginCredentials(User user) {
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
      User userById = userRepository.findByid(id);

      String uniqueErrorMessage = "User with %s not found!";
      if (userById == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(uniqueErrorMessage, id));
      }
      return userById;
  }

  public User editUserbyUserID(User user) {
      Long userid = user.getId();
      String username = user.getUsername();
      Date birthday = user.getBirthday();

      User userbyID = userRepository.findByid(userid);

      String uniqueErrorMessage = "%s user id not found. Please register!";
      if (userbyID == null) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(uniqueErrorMessage, userid));
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
      User userbyID = userRepository.findByid(userid);
      userbyID.setStatus(UserStatus.OFFLINE);
      return userbyID;
  }
}
