package com.halildurmus.hotdeals.report;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.deal.DealRepository;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.report.deal.DealReportRepository;
import com.halildurmus.hotdeals.report.dummy.DummyDealReports;
import com.halildurmus.hotdeals.user.UserRepository;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

public class DealReportIntegrationTest extends BaseIntegrationTest {

  @Autowired private CacheManager cacheManager;

  @Autowired private DealRepository dealRepository;

  @Autowired private DealReportRepository dealReportRepository;

  @Autowired private MockMvc mvc;

  @Autowired private UserRepository userRepository;

  @AfterEach
  void cleanUp() {
    dealRepository.deleteAll();
    dealReportRepository.deleteAll();
    userRepository.deleteAll();
    for (var name : cacheManager.getCacheNames()) {
      var cache = cacheManager.getCache(name);
      if (cache != null) {
        cache.clear();
      }
    }
  }

  @Test
  @DisplayName("GET /deal-reports (returns empty)")
  public void getDealReportsReturnsEmptyArray() throws Exception {
    var request =
        get("/deal-reports")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.deal-reports", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deal-reports (returns 1 deal report)")
  public void getDealReportsReturnsOneDealReport() throws Exception {
    var user = userRepository.save(DummyUsers.user1);
    var deal = dealRepository.save(DummyDeals.deal1);
    var dealReport = DummyDealReports.dealReport1;
    dealReport.setReportedDeal(deal);
    dealReport.setReportedBy(user);
    dealReportRepository.save(dealReport);
    var request =
        get("/deal-reports")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.deal-reports", hasSize(1)))
        .andExpect(jsonPath("$._embedded.deal-reports[0].*", hasSize(8)))
        .andExpect(jsonPath("$._embedded.deal-reports[0].id").isNotEmpty())
        .andExpect(jsonPath("$._embedded.deal-reports[0].reportedBy.id").value(user.getId()))
        .andExpect(jsonPath("$._embedded.deal-reports[0].reportedDeal.id").value(deal.getId()))
        .andExpect(jsonPath("$._embedded.deal-reports[0].reasons", hasSize(2)))
        .andExpect(jsonPath("$._embedded.deal-reports[0].message").value(dealReport.getMessage()))
        .andExpect(jsonPath("$._embedded.deal-reports[0].createdAt").isNotEmpty())
        .andExpect(jsonPath("$._embedded.deal-reports[0].updatedAt").isNotEmpty());
  }
}
