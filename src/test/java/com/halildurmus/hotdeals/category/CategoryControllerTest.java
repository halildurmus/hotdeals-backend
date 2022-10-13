package com.halildurmus.hotdeals.category;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseControllerUnitTest;
import com.halildurmus.hotdeals.category.dto.CategoryPostDTO;
import com.halildurmus.hotdeals.category.dummy.DummyCategories;
import com.halildurmus.hotdeals.exception.CategoryNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapperImpl;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.util.NestedServletException;

@Import({CategoryController.class, MapStructMapperImpl.class})
public class CategoryControllerTest extends BaseControllerUnitTest {

  @Autowired private MapStructMapperImpl mapStructMapper;

  @Autowired private JacksonTester<CategoryPostDTO> json;

  @Autowired private MockMvc mvc;

  @MockBean private CategoryService service;

  @Test
  @DisplayName("GET /categories (returns empty array)")
  public void getCategoriesReturnsEmptyArray() throws Exception {
    when(service.findAll(any(Pageable.class))).thenReturn(Page.empty());
    var request = get("/categories");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /categories (returns 2 categories)")
  public void getCategoriesReturnsTwoCategories() throws Exception {
    var category1 = DummyCategories.category1;
    var category2 = DummyCategories.category2;
    var pagedCategories = new PageImpl<>(List.of(category1, category2));
    when(service.findAll(any(Pageable.class))).thenReturn(pagedCategories);
    var request = get("/categories");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].*", hasSize(6)))
        .andExpect(jsonPath("$[0].id").value(category1.getId()))
        .andExpect(jsonPath("$[0].names").value(equalTo(asParsedJson(category1.getNames()))))
        .andExpect(jsonPath("$[0].parent").value(category1.getParent()))
        .andExpect(jsonPath("$[0].category").value(category1.getCategory()))
        .andExpect(jsonPath("$[0].iconFontFamily").value(category1.getIconFontFamily()))
        .andExpect(jsonPath("$[0].iconLigature").value(category1.getIconLigature()))
        .andExpect(jsonPath("$[1].*", hasSize(6)))
        .andExpect(jsonPath("$[1].id").value(category2.getId()))
        .andExpect(jsonPath("$[1].names").value(equalTo(asParsedJson(category2.getNames()))))
        .andExpect(jsonPath("$[1].parent").value(category2.getParent()))
        .andExpect(jsonPath("$[1].category").value(category2.getCategory()))
        .andExpect(jsonPath("$[1].iconFontFamily").value(category2.getIconFontFamily()))
        .andExpect(jsonPath("$[1].iconLigature").value(category2.getIconLigature()));
  }

  @Test
  @DisplayName("GET /categories/{id}")
  public void returnsGivenCategory() throws Exception {
    var category = DummyCategories.category1;
    when(service.findById(category.getId())).thenReturn(Optional.of(category));
    var request = get("/categories/" + category.getId());

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(6)))
        .andExpect(jsonPath("$.id").value(category.getId()))
        .andExpect(jsonPath("$.names").value(equalTo(asParsedJson(category.getNames()))))
        .andExpect(jsonPath("$.parent").value(category.getParent()))
        .andExpect(jsonPath("$.category").value(category.getCategory()))
        .andExpect(jsonPath("$.iconLigature").value(category.getIconLigature()))
        .andExpect(jsonPath("$.iconFontFamily").value(category.getIconFontFamily()));
  }

  @Test
  @DisplayName("GET /categories/{id} (category not found)")
  public void getCategoryThrowsCategoryNotFoundException() {
    var category = DummyCategories.category1;
    when(service.findById(category.getId())).thenReturn(Optional.empty());
    var request = get("/categories/" + category.getId());

    assertThrows(
        CategoryNotFoundException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }

  @Test
  @DisplayName("POST /categories")
  public void createsCategory() throws Exception {
    var category = DummyCategories.category1;
    var categoryPostDTO = mapStructMapper.categoryToCategoryPostDTO(category);
    when(service.create(any(Category.class))).thenReturn(category);

    var request =
        post("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .content(json.write(categoryPostDTO).getJson())
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(6)))
        .andExpect(jsonPath("$.id").value(category.getId()))
        .andExpect(jsonPath("$.names").value(equalTo(asParsedJson(category.getNames()))))
        .andExpect(jsonPath("$.parent").value(category.getParent()))
        .andExpect(jsonPath("$.category").value(category.getCategory()))
        .andExpect(jsonPath("$.iconLigature").value(category.getIconLigature()))
        .andExpect(jsonPath("$.iconFontFamily").value(category.getIconFontFamily()));
  }

  @Test
  @DisplayName("POST /categories (empty body)")
  public void postCategoryValidationFails() throws Exception {
    var request =
        post("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .content("{}")
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'categoryPostDTO' on field 'names'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'categoryPostDTO' on field 'parent'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'categoryPostDTO' on field 'category'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains(
                            "Field error in object 'categoryPostDTO' on field 'iconLigature'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains(
                            "Field error in object 'categoryPostDTO' on field 'iconFontFamily'")));
  }

  @Test
  @DisplayName("POST /categories (missing English translation)")
  public void postCategoryValidationFailsDueToMissingEnglishTranslation() throws Exception {
    var categoryPostDTO =
        mapStructMapper.categoryToCategoryPostDTO(
            DummyCategories.category1WithoutEnglishTranslation);
    var request =
        post("/categories")
            .accept(MediaType.APPLICATION_JSON)
            .content(json.write(categoryPostDTO).getJson())
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(status().reason(equalTo("The category name must have an English translation!")));
  }

  @Test
  @DisplayName("PUT /categories/{id}")
  public void updatesGivenCategory() throws Exception {
    var category = DummyCategories.category1;
    var categoryPostDTO = mapStructMapper.categoryToCategoryPostDTO(category);
    when(service.findById(anyString())).thenReturn(Optional.of(category));
    when(service.update(any(Category.class))).thenReturn(category);
    var request =
        put("/categories/" + category.getId())
            .accept(MediaType.APPLICATION_JSON)
            .content(json.write(categoryPostDTO).getJson())
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(6)))
        .andExpect(jsonPath("$.id").value(category.getId()))
        .andExpect(jsonPath("$.names").value(equalTo(asParsedJson(category.getNames()))))
        .andExpect(jsonPath("$.parent").value(category.getParent()))
        .andExpect(jsonPath("$.category").value(category.getCategory()))
        .andExpect(jsonPath("$.iconLigature").value(category.getIconLigature()))
        .andExpect(jsonPath("$.iconFontFamily").value(category.getIconFontFamily()));
  }

  @Test
  @DisplayName("PUT /categories/{id} (empty body)")
  public void putCategoryValidationFails() throws Exception {
    var id = DummyCategories.category1.getId();
    var request =
        put("/categories/" + id)
            .accept(MediaType.APPLICATION_JSON)
            .content("{}")
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'categoryPostDTO' on field 'names'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'categoryPostDTO' on field 'parent'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'categoryPostDTO' on field 'category'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains(
                            "Field error in object 'categoryPostDTO' on field 'iconLigature'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains(
                            "Field error in object 'categoryPostDTO' on field 'iconFontFamily'")));
  }

  @Test
  @DisplayName("DELETE /categories/{id}")
  public void deletesGivenCategory() throws Exception {
    var id = DummyCategories.category1.getId();
    var request = delete("/categories/" + id);
    mvc.perform(request).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /categories/{id} (invalid id)")
  public void deleteCategoryThrowsConstraintViolationException() {
    var id = "23478fsf234";
    var request = delete("/categories/" + id);

    assertThrows(
        ConstraintViolationException.class,
        () -> {
          try {
            mvc.perform(request);
          } catch (NestedServletException e) {
            throw e.getCause();
          }
        });
  }
}
