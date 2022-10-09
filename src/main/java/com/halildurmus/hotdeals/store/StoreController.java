package com.halildurmus.hotdeals.store;

import com.halildurmus.hotdeals.exception.StoreNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.security.role.IsSuper;
import com.halildurmus.hotdeals.store.dto.StoreGetDTO;
import com.halildurmus.hotdeals.store.dto.StorePostDTO;
import com.halildurmus.hotdeals.util.IsObjectId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springdoc.api.annotations.ParameterObject;
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

@Tag(name = "stores")
@RestController
@RequestMapping("/stores")
@Validated
public class StoreController {

  @Autowired private MapStructMapper mapStructMapper;

  @Autowired private StoreService service;

  @GetMapping
  @Operation(summary = "Returns all stores")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Successful operation",
          content =
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = StoreGetDTO.class)))))
  public List<StoreGetDTO> getStores(@ParameterObject Pageable pageable) {
    final Page<Store> stores = service.findAll(pageable);

    return stores.getContent().stream()
        .map(mapStructMapper::storeToStoreGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  @IsSuper
  @Operation(summary = "Finds store by ID", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StoreGetDTO.class))),
    @ApiResponse(responseCode = "400", description = "Invalid store ID", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    @ApiResponse(responseCode = "404", description = "Store not found", content = @Content)
  })
  public StoreGetDTO getStore(
      @Parameter(
              description = "String representation of the Store ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id) {
    final Store store = service.findById(id).orElseThrow(StoreNotFoundException::new);

    return mapStructMapper.storeToStoreGetDTO(store);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @IsSuper
  @Operation(summary = "Creates a store", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "The store created successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StoreGetDTO.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  })
  public StoreGetDTO createStore(@Valid @RequestBody StorePostDTO storePostDTO) {
    final Store store = service.create(mapStructMapper.storePostDTOToStore(storePostDTO));

    return mapStructMapper.storeToStoreGetDTO(store);
  }

  @PutMapping("/{id}")
  @IsSuper
  @Operation(
      summary = "Updates an existing store",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "The store successfully updated",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StoreGetDTO.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    @ApiResponse(responseCode = "404", description = "Store not found", content = @Content)
  })
  public StoreGetDTO updateStore(
      @Parameter(
              description = "String representation of the Store ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id,
      @Valid @RequestBody StorePostDTO storePostDTO) {
    final Store store = convertToEntity(id, storePostDTO);

    return mapStructMapper.storeToStoreGetDTO(service.update(store));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @IsSuper
  @Operation(
      summary = "Deletes an existing store",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "204",
        description = "The store successfully deleted",
        content = @Content),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    @ApiResponse(responseCode = "404", description = "Store not found", content = @Content)
  })
  public void deleteStore(
      @Parameter(
              description = "String representation of the Store ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id) {
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
