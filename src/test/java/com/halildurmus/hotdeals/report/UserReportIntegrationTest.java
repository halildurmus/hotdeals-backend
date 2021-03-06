package com.halildurmus.hotdeals.report;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.report.dummy.DummyUserReports;
import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

public class UserReportIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MockMvc mvc;

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection("deals");
    mongoTemplate.dropCollection("reports");
    mongoTemplate.dropCollection("users");
    for (String name : cacheManager.getCacheNames()) {
      Cache cache = cacheManager.getCache(name);
      if (cache != null) {
        cache.clear();
      }
    }
  }

  @Test
  @DisplayName("GET /user-reports (returns empty)")
  public void getUserReportsReturnsEmptyArray() throws Exception {
    final RequestBuilder requestBuilder = get("/user-reports")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.user-reports", hasSize(0)));
  }

  @Test
  @DisplayName("GET /user-reports (returns 1 user report)")
  public void getUserReportsReturnsOneUser() throws Exception {
    final User user1 = mongoTemplate.insert(DummyUsers.user1);
    final User user2 = mongoTemplate.insert(DummyUsers.user2);
    final UserReport userReport = DummyUserReports.userReport1;
    userReport.setReportedBy(user1);
    userReport.setReportedUser(user2);
    mongoTemplate.insert(userReport);
    final RequestBuilder requestBuilder = get("/user-reports")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.user-reports", hasSize(1)))
        .andExpect(jsonPath("$._embedded.user-reports[0].*", hasSize(8)))
        .andExpect(jsonPath("$._embedded.user-reports[0].id").isNotEmpty())
        .andExpect(jsonPath("$._embedded.user-reports[0].reportedBy.id").value(user1.getId()))
        .andExpect(jsonPath("$._embedded.user-reports[0].reportedUser.id").value(user2.getId()))
        .andExpect(jsonPath("$._embedded.user-reports[0].reasons", hasSize(2)))
        .andExpect(jsonPath("$._embedded.user-reports[0].message").value(userReport.getMessage()))
        .andExpect(jsonPath("$._embedded.user-reports[0].createdAt").isNotEmpty())
        .andExpect(jsonPath("$._embedded.user-reports[0].updatedAt").isNotEmpty());
  }

}