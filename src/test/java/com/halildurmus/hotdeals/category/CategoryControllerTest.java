package com.halildurmus.hotdeals.category;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.halildurmus.hotdeals.BaseControllerUnitTest;
import com.halildurmus.hotdeals.category.DTO.CategoryPostDTO;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.util.NestedServletException;

@Import({CategoryController.class, MapStructMapperImpl.class})
public class CategoryControllerTest extends BaseControllerUnitTest {

  private final MapStructMapperImpl mapStructMapper = new MapStructMapperImpl();

  @Autowired
  private JacksonTester<CategoryPostDTO> json;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private CategoryService service;

  @Test
  @DisplayName("GET /categories (returns empty array)")
  public void returnsEmptyArray() throws Exception {
    when(service.findAll(isA(Pageable.class))).thenReturn(Page.empty());

    final RequestBuilder request = get("/categories");
    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /categories (returns 2 categories)")
  public void returnsTwoCategories() throws Exception {
    final Page<Category> pagedCategories = new PageImpl<>(
        List.of(DummyCategories.category1, DummyCategories.category2));
    when(service.findAll(isA(Pageable.class))).thenReturn(pagedCategories);

    final RequestBuilder request = get("/categories");
    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].category").value(DummyCategories.category1.getCategory()))
        .andExpect(jsonPath("$[1].category").value(DummyCategories.category2.getCategory()));
  }

  @Test
  @DisplayName("GET /categories/{id}")
  public void returnsSpecificCategory() throws Exception {
    final Category category = DummyCategories.categoryWithId;
    when(service.findById(category.getId())).thenReturn(Optional.of(category));

    final RequestBuilder request = get("/categories/" + category.getId());
    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.names").value(equalTo(asParsedJson(category.getNames()))))
        .andExpect(jsonPath("$.parent").value(category.getParent()))
        .andExpect(jsonPath("$.category").value(category.getCategory()))
        .andExpect(jsonPath("$.iconLigature").value(category.getIconLigature()))
        .andExpect(jsonPath("$.iconFontFamily").value(category.getIconFontFamily()));
  }

  @Test
  @DisplayName("GET /categories/{id} (category not found)")
  public void throwsCategoryNotFoundException() {
    final Category category = DummyCategories.categoryWithId;
    when(service.findById(category.getId())).thenReturn(Optional.empty());
    final RequestBuilder request = get("/categories/" + category.getId());

    assertThrows(CategoryNotFoundException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("POST /categories (success)")
  public void createsCategory() throws Exception {
    final CategoryPostDTO categoryPostDTO = mapStructMapper.categoryToCategoryPostDTO(
        DummyCategories.category3);
    when(service.create(isA(Category.class))).thenReturn(DummyCategories.category3);

    final RequestBuilder request = post("/categories")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(categoryPostDTO).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.names").value(equalTo(asParsedJson(categoryPostDTO.getNames()))))
        .andExpect(jsonPath("$.parent").value(categoryPostDTO.getParent()))
        .andExpect(jsonPath("$.category").value(categoryPostDTO.getCategory()))
        .andExpect(jsonPath("$.iconLigature").value(categoryPostDTO.getIconLigature()))
        .andExpect(jsonPath("$.iconFontFamily").value(categoryPostDTO.getIconFontFamily()));
  }

  @Test
  @DisplayName("POST /categories (validation fails)")
  public void postValidationFails() throws Exception {
    final RequestBuilder request = post("/categories")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("5 errors")));
  }

  @Test
  @DisplayName("POST /categories (missing English translation)")
  public void postValidationFailsDueToMissingEnglishTranslation() throws Exception {
    final CategoryPostDTO categoryPostDTO = mapStructMapper.categoryToCategoryPostDTO(
        DummyCategories.categoryWithoutEnglishTranslation);

    final RequestBuilder request = post("/categories")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(categoryPostDTO).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(status().reason(equalTo("The category name must have an English translation!")));
  }

  @Test
  @DisplayName("PUT /categories/{id}")
  public void updatesSpecificCategory() throws Exception {
    final CategoryPostDTO categoryPostDTO = mapStructMapper.categoryToCategoryPostDTO(
        DummyCategories.category2);
    when(service.update(isA(Category.class))).thenReturn(DummyCategories.category2);
    final String id = DummyCategories.categoryWithId.getId();

    final RequestBuilder request = put("/categories/" + id)
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(categoryPostDTO).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isOk())
        .andExpect(jsonPath("$.names").value(equalTo(asParsedJson(categoryPostDTO.getNames()))))
        .andExpect(jsonPath("$.parent").value(categoryPostDTO.getParent()))
        .andExpect(jsonPath("$.category").value(categoryPostDTO.getCategory()))
        .andExpect(jsonPath("$.iconLigature").value(categoryPostDTO.getIconLigature()))
        .andExpect(jsonPath("$.iconFontFamily").value(categoryPostDTO.getIconFontFamily()));
  }

  @Test
  @DisplayName("PUT /categories/{id} (validation fails)")
  public void putValidationFails() throws Exception {
    final String id = DummyCategories.categoryWithId.getId();
    final RequestBuilder request = put("/categories/" + id)
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("5 errors")));
  }

  @Test
  @DisplayName("DELETE /categories/{id}")
  public void deletesSpecificCategory() throws Exception {
    final String id = DummyCategories.categoryWithId.getId();
    final RequestBuilder request = delete("/categories/" + id);
    mvc.perform(request).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /categories/{id} (invalid id)")
  public void throwsConstraintViolationExceptionDueToInvalidId() {
    final String id = "23478fsf234";
    final RequestBuilder request = delete("/categories/" + id);

    assertThrows(ConstraintViolationException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

}

