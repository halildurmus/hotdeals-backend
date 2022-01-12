package com.halildurmus.hotdeals.store;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.store.dummy.DummyStores;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

public class StoreIntegrationTest extends BaseIntegrationTest {

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

  @Test
  @DisplayName("POST /stores")
  public void createsStore() throws Exception {
    final Store store = DummyStores.store1;
    final RequestBuilder requestBuilder = post("/stores")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(store).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").value(store.getName()))
        .andExpect(jsonPath("$.logo").value(store.getLogo()));
  }

  @Test
  @DisplayName("GET /stores (returns empty)")
  public void getStoresReturnsEmptyArray() throws Exception {
    final RequestBuilder requestBuilder = get("/stores")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /stores (returns 1 store)")
  public void getStoresReturnsOneStore() throws Exception {
    final Store store = DummyStores.store1;
    mongoTemplate.insert(store);
    final RequestBuilder requestBuilder = get("/stores")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].*", hasSize(3)))
        .andExpect(jsonPath("$[0].id").isNotEmpty())
        .andExpect(jsonPath("$[0].name").value(store.getName()))
        .andExpect(jsonPath("$[0].logo").value(store.getLogo()));
  }

}
