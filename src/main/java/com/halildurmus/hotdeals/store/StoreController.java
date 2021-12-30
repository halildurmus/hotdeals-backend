package com.halildurmus.hotdeals.store;

import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.store.DTO.StoreGetDTO;
import com.halildurmus.hotdeals.store.DTO.StorePostDTO;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
@Validated
public class StoreController {

  @Autowired
  private MapStructMapper mapStructMapper;

  @Autowired
  private StoreService service;

  @GetMapping("/stores")
  public ResponseEntity<List<StoreGetDTO>> getStores(Pageable pageable) {
    final Page<Store> stores = service.findAll(pageable);
    final List<StoreGetDTO> storeGetDTOs = stores.getContent().stream()
        .map(store -> mapStructMapper.storeToStoreGetDTO(store)).collect(
            Collectors.toList());

    return ResponseEntity.ok(storeGetDTOs);
  }

  @GetMapping("/stores/{id}")
  public ResponseEntity<StoreGetDTO> getStore(@ObjectIdConstraint @PathVariable String id) {
    final Optional<Store> store = service.findById(id);
    if (store.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok().body(mapStructMapper.storeToStoreGetDTO(store.get()));
  }

  @PostMapping("/stores")
  public ResponseEntity<StoreGetDTO> createStore(@Valid @RequestBody StorePostDTO storePostDTO) {
    final Store store = service.save(mapStructMapper.storePostDTOStore(storePostDTO));

    return ResponseEntity.status(201).body(mapStructMapper.storeToStoreGetDTO(store));
  }

}
