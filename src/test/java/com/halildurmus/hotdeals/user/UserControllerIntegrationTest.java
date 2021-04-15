package com.halildurmus.hotdeals.user;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ActiveProfiles("integration-test")
@AutoConfigureJsonTesters
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

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
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/users")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(DummyUsers.user4WithoutNickname).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$.nickname").value(not(empty())));
  }

  @Test
  @DisplayName("GET /users (returns empty)")
  public void shouldReturnEmptyArray() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/users")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(
        requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.users", hasSize(0)));
  }

  @Test
  @DisplayName("GET /users (returns 1 user)")
  public void shouldReturnOneUserInArray() throws Exception {
    mongoTemplate.insert(DummyUsers.user1);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/users")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(
        requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.users", hasSize(1)))
        .andExpect(jsonPath("$._embedded.users[0].email").value(DummyUsers.user1.getEmail()));
  }
}
