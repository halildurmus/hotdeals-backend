package com.halildurmus.hotdeals.report;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.report.dummy.DummyUserReports;
import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
@AutoConfigureMockMvc(addFilters = false)
public class UserReportControllerIntegrationTest extends BaseIntegrationTest {

  static User fakeUser = new User("607345b0eeeee1452898128b");

  @Autowired
  CacheManager cacheManager;

  @MockBean
  SecurityService securityService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JacksonTester<UserReport> json;

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection("reports");
    System.out.println(cacheManager.getCacheNames());
    for (String name : cacheManager.getCacheNames()) {
      Cache cache = cacheManager.getCache(name);
      if (cache != null) {
        cache.clear();
      }
    }
  }

  // TODO: Write test cases for validating mandatory fields.

  @Test
  @DisplayName("POST /user-reports")
  public void shouldCreateUserReportThenReturnUserReport() throws Exception {
    Mockito.when(securityService.getUser()).thenReturn(fakeUser);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/user-reports")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(DummyUserReports.userReport1).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(not(empty())))
        .andExpect(jsonPath("$.reportedBy").value(fakeUser.getId()))
        .andExpect(jsonPath("$.reportedUser").value(
            DummyUserReports.userReport1.getReportedUser().toString()))
        .andExpect(jsonPath("$.message").value(DummyUserReports.userReport1.getMessage()));
  }

  @Test
  @DisplayName("GET /user-reports (returns empty)")
  public void shouldReturnEmptyArray() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/user-reports")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.user-reports", hasSize(0)));
  }

  @Test
  @DisplayName("GET /user-reports (returns 1 user report)")
  public void shouldReturnOneUserReportInArray() throws Exception {
    Mockito.when(securityService.getUser()).thenReturn(fakeUser);

    mongoTemplate.insert(DummyUserReports.userReport1);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/user-reports")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.user-reports", hasSize(1)))
        .andExpect(jsonPath("$._embedded.user-reports[0].id").value(not(empty())))
        .andExpect(jsonPath("$._embedded.user-reports[0].reportedBy").value(fakeUser.getId()))
        .andExpect(jsonPath("$._embedded.user-reports[0].reportedUser").value(
            DummyUserReports.userReport1.getReportedUser().toString()))
        .andExpect(
            jsonPath("$._embedded.user-reports[0].message").value(
                DummyUserReports.userReport1.getMessage()));
  }
}