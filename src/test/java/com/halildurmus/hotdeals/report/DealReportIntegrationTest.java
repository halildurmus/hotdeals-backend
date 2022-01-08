package com.halildurmus.hotdeals.report;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.report.deal.DealReport;
import com.halildurmus.hotdeals.report.dummy.DummyDealReports;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class DealReportIntegrationTest extends BaseIntegrationTest {

  static User fakeUser = User.builder().id("607345b0eeeee1452898128b").build();

  @Autowired
  CacheManager cacheManager;

  @MockBean
  SecurityService securityService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JacksonTester<DealReport> json;

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
  @DisplayName("POST /deal-reports")
  public void shouldCreateDealReportThenReturnDealReport() throws Exception {
    Mockito.when(securityService.getUser()).thenReturn(fakeUser);

    final Deal deal = mongoTemplate.insert(DummyDeals.deal1);
    final DealReport dealReport = DummyDealReports.dealReport1;
    dealReport.setReportedDeal(deal);

    final RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/deal-reports")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(dealReport).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$.id").value(not(empty())))
        .andExpect(jsonPath("$.reportedBy").value(fakeUser.getId()))
        .andExpect(jsonPath("$.reportedDeal").value(
            DummyDealReports.dealReport1.getReportedDeal().toString()))
        .andExpect(jsonPath("$.message").value(DummyDealReports.dealReport1.getMessage()));
  }

  @Test
  @DisplayName("GET /deal-reports (returns empty)")
  public void shouldReturnEmptyArray() throws Exception {
    final RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/deal-reports")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.deal-reports", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deal-reports (returns 1 deal report)")
  public void shouldReturnOneDealReportInArray() throws Exception {
    Mockito.when(securityService.getUser()).thenReturn(fakeUser);

    mongoTemplate.insert(DummyDealReports.dealReport1);

    final RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/deal-reports")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.deal-reports", hasSize(1)))
        .andExpect(jsonPath("$._embedded.deal-reports[0].id").value(not(empty())))
        .andExpect(jsonPath("$._embedded.deal-reports[0].reportedBy").value(fakeUser.getId()))
        .andExpect(jsonPath("$._embedded.deal-reports[0].reportedDeal").value(
            DummyDealReports.dealReport1.getReportedDeal().toString()))
        .andExpect(
            jsonPath("$._embedded.deal-reports[0].message").value(
                DummyDealReports.dealReport1.getMessage()));
  }
}