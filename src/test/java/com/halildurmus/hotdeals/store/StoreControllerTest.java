package com.halildurmus.hotdeals.store;

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
  public void getStoresReturnsEmptyArray() throws Exception {
    when(service.findAll(any(Pageable.class))).thenReturn(Page.empty());
    final RequestBuilder request = get("/stores");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /stores (returns 2 stores)")
  public void getStoresReturnsTwoStores() throws Exception {
    final Page<Store> pagedStores = new PageImpl<>(
        List.of(DummyStores.store1, DummyStores.store2));
    when(service.findAll(any(Pageable.class))).thenReturn(pagedStores);
    final RequestBuilder request = get("/stores");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].*", hasSize(3)))
        .andExpect(jsonPath("$[0].id").value(DummyStores.store1.getId()))
        .andExpect(jsonPath("$[0].name").value(DummyStores.store1.getName()))
        .andExpect(jsonPath("$[0].logo").value(DummyStores.store1.getLogo()))
        .andExpect(jsonPath("$[1].*", hasSize(3)))
        .andExpect(jsonPath("$[1].id").value(DummyStores.store2.getId()))
        .andExpect(jsonPath("$[1].name").value(DummyStores.store2.getName()))
        .andExpect(jsonPath("$[1].logo").value(DummyStores.store2.getLogo()));
  }

  @Test
  @DisplayName("GET /stores/{id}")
  public void returnsGivenStore() throws Exception {
    final Store store = DummyStores.store1;
    when(service.findById(store.getId())).thenReturn(Optional.of(store));
    final RequestBuilder request = get("/stores/" + store.getId());

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$.id").value(store.getId()))
        .andExpect(jsonPath("$.name").value(store.getName()))
        .andExpect(jsonPath("$.logo").value(store.getLogo()));
  }

  @Test
  @DisplayName("GET /stores/{id} (store not found)")
  public void getStoreThrowsStoreNotFoundException() {
    final Store store = DummyStores.store1;
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
  @DisplayName("POST /stores")
  public void createsStore() throws Exception {
    final Store store = DummyStores.store1;
    final StorePostDTO storePostDTO = mapStructMapper.storeToStorePostDTO(store);
    when(service.create(any(Store.class))).thenReturn(store);
    final RequestBuilder request = post("/stores")
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(storePostDTO).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$.id").value(store.getId()))
        .andExpect(jsonPath("$.name").value(store.getName()))
        .andExpect(jsonPath("$.logo").value(store.getLogo()));
  }

  @Test
  @DisplayName("POST /stores (empty body)")
  public void postStoreValidationFails() throws Exception {
    final RequestBuilder request = post("/stores")
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'storePostDTO' on field 'name'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'storePostDTO' on field 'logo'")));
  }

  @Test
  @DisplayName("PUT /stores/{id}")
  public void updatesGivenStore() throws Exception {
    final Store store = DummyStores.store2;
    final StorePostDTO storePostDTO = mapStructMapper.storeToStorePostDTO(store);
    when(service.findById(anyString())).thenReturn(Optional.of(store));
    when(service.update(any(Store.class))).thenReturn(store);
    final RequestBuilder request = put("/stores/" + store.getId())
        .accept(MediaType.APPLICATION_JSON)
        .content(json.write(storePostDTO).getJson())
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$.id").value(store.getId()))
        .andExpect(jsonPath("$.name").value(store.getName()))
        .andExpect(jsonPath("$.logo").value(store.getLogo()));
  }

  @Test
  @DisplayName("PUT /stores/{id} (empty body)")
  public void putStoreValidationFails() throws Exception {
    final String id = DummyStores.store1.getId();
    final RequestBuilder request = put("/stores/" + id)
        .accept(MediaType.APPLICATION_JSON)
        .content("{}")
        .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request).andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'storePostDTO' on field 'name'")))
        .andExpect(result -> assertTrue(
            Objects.requireNonNull(result.getResolvedException()).getMessage()
                .contains("Field error in object 'storePostDTO' on field 'logo'")));
  }

  @Test
  @DisplayName("DELETE /stores/{id}")
  public void deletesGivenStore() throws Exception {
    final String id = DummyStores.store1.getId();
    final RequestBuilder request = delete("/stores/" + id);
    mvc.perform(request).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /stores/{id} (invalid id)")
  public void deleteStoreThrowsConstraintViolationException() {
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

