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
import com.halildurmus.hotdeals.store.dto.StorePostDTO;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.util.NestedServletException;

@Import({StoreController.class, MapStructMapperImpl.class})
public class StoreControllerTest extends BaseControllerUnitTest {

  @Autowired private JacksonTester<StorePostDTO> json;

  @Autowired private MapStructMapperImpl mapStructMapper;

  @Autowired private MockMvc mvc;

  @MockBean private StoreService storeService;

  @Test
  @DisplayName("GET /stores (returns empty array)")
  public void getStoresReturnsEmptyArray() throws Exception {
    when(storeService.findAll(any(Pageable.class))).thenReturn(Page.empty());
    var request = get("/stores");

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("GET /stores (returns 2 stores)")
  public void getStoresReturnsTwoStores() throws Exception {
    var pagedStores = new PageImpl<>(List.of(DummyStores.store1, DummyStores.store2));
    when(storeService.findAll(any(Pageable.class))).thenReturn(pagedStores);
    var request = get("/stores");

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
    var store = DummyStores.store1;
    when(storeService.findById(store.getId())).thenReturn(Optional.of(store));
    var request = get("/stores/" + store.getId());

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
    var store = DummyStores.store1;
    when(storeService.findById(store.getId())).thenReturn(Optional.empty());
    var request = get("/stores/" + store.getId());

    assertThrows(
        StoreNotFoundException.class,
        () -> {
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
    var store = DummyStores.store1;
    var storePostDTO = mapStructMapper.storeToStorePostDTO(store);
    when(storeService.create(any(Store.class))).thenReturn(store);
    var request =
        post("/stores")
            .accept(MediaType.APPLICATION_JSON)
            .content(json.write(storePostDTO).getJson())
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isCreated())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$.id").value(store.getId()))
        .andExpect(jsonPath("$.name").value(store.getName()))
        .andExpect(jsonPath("$.logo").value(store.getLogo()));
  }

  @Test
  @DisplayName("POST /stores (empty body)")
  public void postStoreValidationFails() throws Exception {
    var request =
        post("/stores")
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
                        .contains("Field error in object 'storePostDTO' on field 'name'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'storePostDTO' on field 'logo'")));
  }

  @Test
  @DisplayName("PUT /stores/{id}")
  public void updatesGivenStore() throws Exception {
    var store = DummyStores.store2;
    var storePostDTO = mapStructMapper.storeToStorePostDTO(store);
    when(storeService.findById(anyString())).thenReturn(Optional.of(store));
    when(storeService.update(any(Store.class))).thenReturn(store);
    var request =
        put("/stores/" + store.getId())
            .accept(MediaType.APPLICATION_JSON)
            .content(json.write(storePostDTO).getJson())
            .contentType(MediaType.APPLICATION_JSON);

    mvc.perform(request)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$.id").value(store.getId()))
        .andExpect(jsonPath("$.name").value(store.getName()))
        .andExpect(jsonPath("$.logo").value(store.getLogo()));
  }

  @Test
  @DisplayName("PUT /stores/{id} (empty body)")
  public void putStoreValidationFails() throws Exception {
    var id = DummyStores.store1.getId();
    var request =
        put("/stores/" + id)
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
                        .contains("Field error in object 'storePostDTO' on field 'name'")))
        .andExpect(
            result ->
                assertTrue(
                    Objects.requireNonNull(result.getResolvedException())
                        .getMessage()
                        .contains("Field error in object 'storePostDTO' on field 'logo'")));
  }

  @Test
  @DisplayName("DELETE /stores/{id}")
  public void deletesGivenStore() throws Exception {
    var id = DummyStores.store1.getId();
    var request = delete("/stores/" + id);
    mvc.perform(request).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /stores/{id} (invalid id)")
  public void deleteStoreThrowsConstraintViolationException() {
    var id = "23478fsf234";
    var request = delete("/stores/" + id);

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
