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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

public class StoreIntegrationTest extends BaseIntegrationTest {

  @Autowired private JacksonTester<Store> json;

  @Autowired private MockMvc mvc;

  @Autowired private StoreRepository storeRepository;

  @AfterEach
  void cleanUp() {
    storeRepository.deleteAll();
  }

  @Test
  @DisplayName("POST /stores")
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN", "SUPER"})
  public void createsStore() throws Exception {
    var store = DummyStores.store1;
    var request =
        post("/stores")
            .accept(MediaType.APPLICATION_JSON)
            .content(json.write(store).getJson())
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
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
    var request =
        get("/stores").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /stores (returns 1 store)")
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN", "SUPER"})
  public void getStoresReturnsOneStore() throws Exception {
    var store = DummyStores.store1;
    storeRepository.save(store);
    var request =
        get("/stores").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].*", hasSize(3)))
        .andExpect(jsonPath("$[0].id").isNotEmpty())
        .andExpect(jsonPath("$[0].name").value(store.getName()))
        .andExpect(jsonPath("$[0].logo").value(store.getLogo()));
  }
}
