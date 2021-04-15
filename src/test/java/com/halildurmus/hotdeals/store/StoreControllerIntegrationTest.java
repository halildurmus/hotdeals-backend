package com.halildurmus.hotdeals.store;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.halildurmus.hotdeals.store.dummy.DummyStores;
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
public class StoreControllerIntegrationTest {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JacksonTester<Store> json;

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection("stores");
  }

  // TODO: Write test cases for validating mandatory fields.

  @Test
  @DisplayName("POST /stores")
  public void shouldCreateStoreThenReturnStore() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/stores")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(DummyStores.store1).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$.name").value(DummyStores.store1.getName()))
        .andExpect(jsonPath("$.logo").value(DummyStores.store1.getLogo()));
  }

  @Test
  @DisplayName("GET /stores (returns empty)")
  public void shouldReturnEmptyArray() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/stores")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(
        requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.stores", hasSize(0)));
  }

  @Test
  @DisplayName("GET /stores (returns 1 store)")
  public void shouldReturnOneStoreInArray() throws Exception {
    mongoTemplate.insert(DummyStores.store1);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/stores")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(
        requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.stores", hasSize(1)))
        .andExpect(jsonPath("$._embedded.stores[0].name").value(DummyStores.store1.getName()))
        .andExpect(jsonPath("$._embedded.stores[0].logo").value(DummyStores.store1.getLogo()));
  }
}
