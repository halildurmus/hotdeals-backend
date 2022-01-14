package com.halildurmus.hotdeals.user;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.report.dummy.DummyUserReports;
import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

public class UserIntegrationTest extends BaseIntegrationTest {

  private final ObjectMapper objectMapper = JsonMapper.builder()
      .findAndAddModules()
      .build();

  @MockBean
  private SecurityService securityService;

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

  @Test
  @DisplayName("POST /users")
  public void createsUser() throws Exception {
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

  @Test
  @DisplayName("POST /users/{id}/reports")
  public void createsUserReport() throws Exception {
    final User user1 = mongoTemplate.insert(DummyUsers.user1);
    final User user2 = mongoTemplate.insert(DummyUsers.user2);
    when(securityService.getUser()).thenReturn(user1);
    final UserReport userReport = DummyUserReports.userReport1;
    userReport.setReportedUser(user2);
    final RequestBuilder requestBuilder = post("/users/" + user2.getId() + "/reports")
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userReport))
        .contentType(MediaType.APPLICATION_JSON);
    mvc.perform(requestBuilder).andExpect(status().isCreated());
  }

}
