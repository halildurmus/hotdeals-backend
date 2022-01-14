package com.halildurmus.hotdeals.store;

import com.halildurmus.hotdeals.exception.StoreNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.store.DTO.StoreGetDTO;
import com.halildurmus.hotdeals.store.DTO.StorePostDTO;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
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

@RestController
@RequestMapping("/stores")
@Validated
public class StoreController {

  @Autowired
  private MapStructMapper mapStructMapper;

  @Autowired
  private StoreService service;

  @GetMapping
  public List<StoreGetDTO> getStores(Pageable pageable) {
    final Page<Store> stores = service.findAll(pageable);

    return stores.getContent().stream()
        .map(mapStructMapper::storeToStoreGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public StoreGetDTO getStore(@ObjectIdConstraint @PathVariable String id) {
    final Store store = service.findById(id).orElseThrow(StoreNotFoundException::new);

    return mapStructMapper.storeToStoreGetDTO(store);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public StoreGetDTO createStore(@Valid @RequestBody StorePostDTO storePostDTO) {
    final Store store = service.create(mapStructMapper.storePostDTOToStore(storePostDTO));

    return mapStructMapper.storeToStoreGetDTO(store);
  }

  @PutMapping("/{id}")
  public StoreGetDTO updateStore(@ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody StorePostDTO storePostDTO) {
    final Store store = convertToEntity(id, storePostDTO);

    return mapStructMapper.storeToStoreGetDTO(service.update(store));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteStore(@ObjectIdConstraint @PathVariable String id) {
    service.delete(id);
  }

  private Store convertToEntity(String id, StorePostDTO storePostDTO) {
    final Store originalStore = service.findById(id).orElseThrow(StoreNotFoundException::new);
    final Store store = mapStructMapper.storePostDTOToStore(storePostDTO);
    store.setId(id);
    store.setCreatedAt(originalStore.getCreatedAt());

    return store;
  }

}
