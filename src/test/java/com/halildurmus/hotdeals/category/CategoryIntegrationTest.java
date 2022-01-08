package com.halildurmus.hotdeals.category;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.halildurmus.hotdeals.BaseIntegrationTest;
import com.halildurmus.hotdeals.category.DTO.CategoryPostDTO;
import com.halildurmus.hotdeals.category.dummy.DummyCategories;
import com.halildurmus.hotdeals.mapstruct.MapStructMapperImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CategoryIntegrationTest extends BaseIntegrationTest {

  private final MapStructMapperImpl mapStructMapper = new MapStructMapperImpl();

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MockMvc mvc;

  @Autowired
  private JacksonTester<CategoryPostDTO> json;

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection("categories");
  }

  // TODO: Write test cases for validating mandatory fields.

  @Test
  @DisplayName("POST /categories (success)")
  public void shouldCreateCategory() throws Exception {
    final CategoryPostDTO categoryPostDTO = mapStructMapper.categoryToCategoryPostDTO(
        DummyCategories.category1);
    final RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("/categories")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(categoryPostDTO).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(
            jsonPath("$.names").value(
                equalTo(asParsedJson(DummyCategories.category1.getNames()))))
        .andExpect(jsonPath("$.parent").value(DummyCategories.category1.getParent()))
        .andExpect(jsonPath("$.category").value(DummyCategories.category1.getCategory()))
        .andExpect(
            jsonPath("$.iconLigature").value(DummyCategories.category1.getIconLigature()))
        .andExpect(
            jsonPath("$.iconFontFamily").value(
                DummyCategories.category1.getIconFontFamily()));
  }

  @Test
  @DisplayName("GET /categories (returns empty)")
  public void shouldReturnEmptyArray() throws Exception {
    final RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/categories")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.categories", hasSize(0)));
  }

  @Test
  @DisplayName("GET /categories (returns 1 category)")
  public void shouldReturnOneCategoryInArray() throws Exception {
    mongoTemplate.insert(DummyCategories.category1);

    final RequestBuilder requestBuilder = MockMvcRequestBuilders
        .get("/categories")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(
            requestBuilder)
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(jsonPath("$._embedded.categories", hasSize(1)))
        .andExpect(jsonPath("$._embedded.categories[0].names").value(
            equalTo(asParsedJson(DummyCategories.category1.getNames()))))
        .andExpect(jsonPath("$._embedded.categories[0].parent").value(
            DummyCategories.category1.getParent()))
        .andExpect(jsonPath("$._embedded.categories[0].category").value(
            DummyCategories.category1.getCategory()))
        .andExpect(jsonPath("$._embedded.categories[0].iconLigature").value(
            DummyCategories.category1.getIconLigature()))
        .andExpect(jsonPath("$._embedded.categories[0].iconFontFamily").value(
            DummyCategories.category1.getIconFontFamily()));
  }
}