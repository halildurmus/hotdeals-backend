package com.halildurmus.hotdeals.user;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

public class UserIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JacksonTester<User> json;

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection("users");
  }

  // TODO: Write test cases for validating mandatory fields.

  @Test
  @DisplayName("POST /create")
  public void shouldCreateUserThenReturnUser() throws Exception {
    final User user = DummyUsers.user1;
    final RequestBuilder requestBuilder = post("/users")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(user).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(5)))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.uid").value(user.getUid()))
        .andExpect(jsonPath("$.avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$.nickname").isNotEmpty())
        .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  @DisplayName("GET /users (returns empty)")
  public void getUsersReturnsEmptyArray() throws Exception {
    final RequestBuilder requestBuilder = get("/users")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  @DisplayName("GET /users (returns 1 user)")
  public void getUsersReturnsOneUser() throws Exception {
    final User user = DummyUsers.user1;
    mongoTemplate.insert(user);
    final RequestBuilder requestBuilder = get("/users")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].*", hasSize(10)))
        .andExpect(jsonPath("$.content[0].id").isNotEmpty())
        .andExpect(jsonPath("$.content[0].uid").value(user.getUid()))
        .andExpect(jsonPath("$.content[0].email").value(user.getEmail()))
        .andExpect(jsonPath("$.content[0].avatar").value(user.getAvatar()))
        .andExpect(jsonPath("$.content[0].nickname").value(user.getNickname()))
        .andExpect(
            jsonPath("$.content[0].favorites").value(equalTo(asParsedJson(user.getFavorites()))))
        .andExpect(jsonPath("$.content[0].blockedUsers").value(
            equalTo(asParsedJson(user.getBlockedUsers()))))
        .andExpect(
            jsonPath("$.content[0].fcmTokens").value(equalTo(asParsedJson(user.getFcmTokens()))))
        .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty())
        .andExpect(jsonPath("$.content[0].updatedAt").isNotEmpty());
  }

}
