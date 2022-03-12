package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.EditUserPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LoginUserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
      // Test:
    // given
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name", is(user.getName())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setName("Test User");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  public void givenUser_validlogin_thenReturnUser() throws Exception {
      User user = new User();
      Date date = new Date();

      // given registered user
      user.setId(1L);
      user.setName("Rupal");
      user.setUsername("rupal.saxena.rs@gmail.com");
      user.setPassword("rupal");
      user.setCreation_date(date);
      user.setStatus(UserStatus.ONLINE);

      LoginUserPostDTO loginUserPostDTO = new LoginUserPostDTO();
      loginUserPostDTO.setUsername("rupal.saxena.rs@gmail.com");
      loginUserPostDTO.setPassword("rupal");

      given(userService.loginCredentials(Mockito.any())).willReturn(user);

      // when
      MockHttpServletRequestBuilder postRequest = post("/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(loginUserPostDTO));

      // then
      mockMvc.perform(postRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id", is(user.getId().intValue())))
              .andExpect(jsonPath("$.name", is(user.getName())))
              .andExpect(jsonPath("$.username", is(user.getUsername())))
              .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  public void givenUser_whenUserId_ReturnFullUserInfo() throws Exception {
      // TODO: date issue fix
      User user = new User();
      Date date = new Date();

      // given user
      user.setId(1L);
      user.setName("Emma");
      user.setUsername("EmmaIsBest");
      user.setPassword("LittleEmma");
      user.setCreation_date(date);
      user.setStatus(UserStatus.ONLINE);
      //user.setBirthday(date);
      given(userService.getUserbyUserID(Mockito.any())).willReturn(user);

      // when
      MockHttpServletRequestBuilder getRequest = get("/users/1")
              .contentType(MediaType.APPLICATION_JSON);

      // then
      mockMvc.perform(getRequest)
              .andExpect(jsonPath("$.id", is(user.getId().intValue())))
              .andExpect(jsonPath("$.username", is(user.getUsername())))
              .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
              //.andExpect(jsonPath("$.creation_date", is(user.getCreation_date())));
              //.andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())));
  }

  @Test
  public void givenUser_whenEdit_ReturnEdited() throws Exception {
      User user = new User();
      Date date = new Date();

      // given user
      user.setId(1L);
      user.setName("SoPra Rest");
      user.setUsername("SoPra@yahoo.com");
      user.setPassword("SoPra@123");
      user.setCreation_date(date);
      user.setStatus(UserStatus.ONLINE);
      user.setBirthday(null);

      EditUserPutDTO editUserPutDTO = new EditUserPutDTO();
      editUserPutDTO.setUsername("SoPra@yahoo.com");
      editUserPutDTO.setBirthday(date);

      given(userService.editUserbyUserID(Mockito.any())).willReturn(user);

      // when
      MockHttpServletRequestBuilder putRequest = put("/users/1")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(editUserPutDTO));

      // then
      mockMvc.perform(putRequest)
              .andExpect(status().isNoContent());
  }

  @Test
  public void GivenId_whenlogout_OfflineStatus() throws Exception {
      User user = new User();

      // given
      user.setId(1L);
      user.setStatus(UserStatus.OFFLINE);
      given(userService.logoutUserbyUserID(Mockito.any())).willReturn(user);

      // when
      MockHttpServletRequestBuilder putRequest = put("/logout/1")
              .contentType(MediaType.APPLICATION_JSON);

      // then
      mockMvc.perform(putRequest)
              .andExpect(jsonPath("$.id", is(user.getId().intValue())))
              .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}