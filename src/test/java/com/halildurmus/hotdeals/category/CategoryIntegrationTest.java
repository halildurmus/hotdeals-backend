package com.halildurmus.hotdeals.category;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.category.dummy.DummyCategories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

public class CategoryIntegrationTest extends BaseIntegrationTest {

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private MockMvc mvc;

  @Autowired private JacksonTester<Category> json;

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection("categories");
  }

  @Test
  @DisplayName("POST /categories")
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN", "SUPER"})
  public void createsCategory() throws Exception {
    final Category category = DummyCategories.category1;
    final RequestBuilder requestBuilder =
        post("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .content(json.write(category).getJson())
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(6)))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.names").value(equalTo(asParsedJson(category.getNames()))))
        .andExpect(jsonPath("$.parent").value(category.getParent()))
        .andExpect(jsonPath("$.category").value(category.getCategory()))
        .andExpect(jsonPath("$.iconLigature").value(category.getIconLigature()))
        .andExpect(jsonPath("$.iconFontFamily").value(category.getIconFontFamily()));
  }

  @Test
  @DisplayName("GET /categories (returns empty)")
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN", "SUPER"})
  public void getCategoriesReturnsEmptyArray() throws Exception {
    final RequestBuilder requestBuilder =
        get("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /categories (returns 1 category)")
  public void getCategoriesReturnsOneCategory() throws Exception {
    final Category category = DummyCategories.category1;
    mongoTemplate.insert(category);
    final RequestBuilder requestBuilder =
        get("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].*", hasSize(6)))
        .andExpect(jsonPath("$[0].id").isNotEmpty())
        .andExpect(jsonPath("$[0].names").value(equalTo(asParsedJson(category.getNames()))))
        .andExpect(jsonPath("$[0].parent").value(category.getParent()))
        .andExpect(jsonPath("$[0].category").value(category.getCategory()))
        .andExpect(jsonPath("$[0].iconLigature").value(category.getIconLigature()))
        .andExpect(jsonPath("$[0].iconFontFamily").value(category.getIconFontFamily()));
  }
}
