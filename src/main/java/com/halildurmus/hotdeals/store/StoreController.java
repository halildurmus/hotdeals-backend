package com.halildurmus.hotdeals.store;

import com.halildurmus.hotdeals.exception.StoreNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.security.role.IsSuper;
import com.halildurmus.hotdeals.store.DTO.StoreGetDTO;
import com.halildurmus.hotdeals.store.DTO.StorePostDTO;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "stores")
@RestController
@RequestMapping("/stores")
@Validated
public class StoreController {

  @Autowired
  private MapStructMapper mapStructMapper;

  @Autowired
  private StoreService service;

  @GetMapping
  @ApiOperation("Returns all stores")
  public List<StoreGetDTO> getStores(Pageable pageable) {
    final Page<Store> stores = service.findAll(pageable);

    return stores.getContent().stream()
        .map(mapStructMapper::storeToStoreGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  @IsSuper
  @ApiOperation(value = "Finds store by ID", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Invalid store ID"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden"),
      @ApiResponse(code = 404, message = "Store not found")
  })
  public StoreGetDTO getStore(
      @ApiParam("String representation of the Store ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String id) {
    final Store store = service.findById(id).orElseThrow(StoreNotFoundException::new);

    return mapStructMapper.storeToStoreGetDTO(store);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @IsSuper
  @ApiOperation(value = "Creates a store", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden")
  })
  public StoreGetDTO createStore(@Valid @RequestBody StorePostDTO storePostDTO) {
    final Store store = service.create(mapStructMapper.storePostDTOToStore(storePostDTO));

    return mapStructMapper.storeToStoreGetDTO(store);
  }

  @PutMapping("/{id}")
  @IsSuper
  @ApiOperation(value = "Updates an existing store", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden"),
      @ApiResponse(code = 404, message = "Store not found")
  })
  public StoreGetDTO updateStore(
      @ApiParam("String representation of the Store ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String id, @Valid @RequestBody StorePostDTO storePostDTO) {
    final Store store = convertToEntity(id, storePostDTO);

    return mapStructMapper.storeToStoreGetDTO(service.update(store));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @IsSuper
  @ApiOperation(value = "Deletes an existing store", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 403, message = "Forbidden"),
      @ApiResponse(code = 404, message = "Store not found")
  })
  public void deleteStore(
      @ApiParam("String representation of the Store ID. e.g. '5fbe790ec6f0b32014074bb1'")
      @ObjectIdConstraint @PathVariable String id) {
    service.delete(id);
  }

  private Store convertToEntity(String id, StorePostDTO storePostDTO) {
    // Fetch the store from the db and set the missing properties from it
    final Store originalStore = service.findById(id).orElseThrow(StoreNotFoundException::new);
    final Store store = mapStructMapper.storePostDTOToStore(storePostDTO);
    store.setId(id);
    store.setCreatedAt(originalStore.getCreatedAt());

    return store;
  }

}
