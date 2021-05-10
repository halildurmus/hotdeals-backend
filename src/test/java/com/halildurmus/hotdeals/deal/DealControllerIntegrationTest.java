package com.halildurmus.hotdeals.deal;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
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
@AutoConfigureMockMvc(addFilters = false)
public class DealControllerIntegrationTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JacksonTester<Deal> json;

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection("deals");
  }

  // TODO: Write test cases for validating mandatory fields.

  @Test
  @DisplayName("POST /deals")
  public void shouldCreateDealThenReturnDeal() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/deals")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(DummyDeals.deal1).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$.postedBy").value(not(empty())));
  }

  @Test
  @DisplayName("GET /deals (returns empty)")
  public void shouldReturnEmptyArray() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/deals")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(
        requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.deals", hasSize(0)));
  }

  @Test
  @DisplayName("GET /deals (returns 1 deal)")
  public void shouldReturnOneDealInArray() throws Exception {
    mongoTemplate.insert(DummyDeals.deal1);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/deals")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(
        requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.deals", hasSize(1)))
        .andExpect(jsonPath("$._embedded.deals[0].title").value(DummyDeals.deal1.getTitle()))
        .andExpect(jsonPath("$._embedded.deals[0].postedBy").value(not(empty())));
  }
}
