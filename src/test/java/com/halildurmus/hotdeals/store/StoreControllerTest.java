package com.halildurmus.hotdeals.store;

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
import com.halildurmus.hotdeals.exception.StoreNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapperImpl;
import com.halildurmus.hotdeals.store.DTO.StorePostDTO;
import com.halildurmus.hotdeals.store.dummy.DummyStores;
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

@Import({StoreController.class, MapStructMapperImpl.class})
public class StoreControllerTest extends BaseControllerUnitTest {

  private final MapStructMapperImpl mapStructMapper = new MapStructMapperImpl();

  @Autowired
  private JacksonTester<StorePostDTO> json;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private StoreService service;

  @Test
  @DisplayName("GET /stores (returns empty array)")
  public void returnsEmptyArray() throws Exception {
    when(service.findAll(isA(Pageable.class))).thenReturn(Page.empty());

    final RequestBuilder request = get("/stores");
    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /stores (returns 2 stores)")
  public void returnsTwoStores() throws Exception {
    final Page<Store> pagedStores = new PageImpl<>(
        List.of(DummyStores.store1, DummyStores.store2));
    when(service.findAll(isA(Pageable.class))).thenReturn(pagedStores);

    final RequestBuilder request = get("/stores");
    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name").value(DummyStores.store1.getName()))
        .andExpect(jsonPath("$[0].logo").value(DummyStores.store1.getLogo()))
        .andExpect(jsonPath("$[1].name").value(DummyStores.store2.getName()))
        .andExpect(jsonPath("$[1].logo").value(DummyStores.store2.getLogo()));
  }

  @Test
  @DisplayName("GET /stores/{id}")
  public void returnsSpecificStore() throws Exception {
    final Store store = DummyStores.storeWithId;
    when(service.findById(store.getId())).thenReturn(Optional.of(store));

    final RequestBuilder request = get("/stores/" + store.getId());
    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.name").value(store.getName()))
        .andExpect(jsonPath("$.logo").value(store.getLogo()));
  }

  @Test
  @DisplayName("GET /stores/{id} (store not found)")
  public void throwsStoreNotFoundException() {
    final Store store = DummyStores.storeWithId;
    when(service.findById(store.getId())).thenReturn(Optional.empty());
    final RequestBuilder request = get("/stores/" + store.getId());

    assertThrows(StoreNotFoundException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

  @Test
  @DisplayName("POST /stores (success)")
  public void createsStore() throws Exception {
    final StorePostDTO storePostDTO = mapStructMapper.storeToStorePostDTO(DummyStores.store1);
    when(service.create(isA(Store.class))).thenReturn(DummyStores.store1);

    final RequestBuilder request = post("/stores")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(storePostDTO).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.name").value(storePostDTO.getName()))
        .andExpect(jsonPath("$.logo").value(storePostDTO.getLogo()));
  }

  @Test
  @DisplayName("POST /stores (validation fails)")
  public void postValidationFails() throws Exception {
    final RequestBuilder request = post("/stores")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("2 errors")));
  }

  @Test
  @DisplayName("PUT /stores/{id}")
  public void updatesSpecificStore() throws Exception {
    final StorePostDTO storePostDTO = mapStructMapper.storeToStorePostDTO(DummyStores.store2);
    when(service.update(isA(Store.class))).thenReturn(DummyStores.store2);
    final String id = DummyStores.storeWithId.getId();

    final RequestBuilder request = put("/stores/" + id)
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(storePostDTO).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(storePostDTO.getName()))
        .andExpect(jsonPath("$.logo").value(storePostDTO.getLogo()));
  }

  @Test
  @DisplayName("PUT /stores/{id} (validation fails)")
  public void putValidationFails() throws Exception {
    final String id = DummyStores.storeWithId.getId();
    final RequestBuilder request = put("/stores/" + id)
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("2 errors")));
  }

  @Test
  @DisplayName("DELETE /stores/{id}")
  public void deletesSpecificStore() throws Exception {
    final String id = DummyStores.storeWithId.getId();
    final RequestBuilder request = delete("/stores/" + id);
    mvc.perform(request).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /stores/{id} (invalid id)")
  public void throwsConstraintViolationExceptionDueToInvalidId() {
    final String id = "23478fsf234";
    final RequestBuilder request = delete("/stores/" + id);

    assertThrows(ConstraintViolationException.class, () -> {
      try {
        mvc.perform(request);
      } catch (NestedServletException e) {
        throw e.getCause();
      }
    });
  }

}

