package com.halildurmus.hotdeals.report;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.report.dummy.DummyUserReports;
import com.halildurmus.hotdeals.report.user.UserReportRepository;
import com.halildurmus.hotdeals.user.UserRepository;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class UserReportIntegrationTest extends BaseIntegrationTest {

  @Autowired private CacheManager cacheManager;

  @Autowired private MockMvc mvc;

  @Autowired private UserReportRepository userReportRepository;

  @Autowired private UserRepository userRepository;

  @AfterEach
  void cleanUp() {
    userRepository.deleteAll();
    userReportRepository.deleteAll();
    for (var name : cacheManager.getCacheNames()) {
      var cache = cacheManager.getCache(name);
      if (cache != null) {
        cache.clear();
      }
    }
  }

  @Test
  @DisplayName("GET /user-reports (returns empty)")
  public void getUserReportsReturnsEmptyArray() throws Exception {
    var request =
        get("/user-reports")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.user-reports", hasSize(0)));
  }

  @Test
  @DisplayName("GET /user-reports (returns 1 user report)")
  public void getUserReportsReturnsOneUser() throws Exception {
    var user1 = userRepository.save(DummyUsers.user1);
    var user2 = userRepository.save(DummyUsers.user2);
    var userReport = DummyUserReports.userReport1;
    userReport.setReportedBy(user1);
    userReport.setReportedUser(user2);
    userReportRepository.save(userReport);
    var request =
        get("/user-reports")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
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
