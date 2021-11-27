package com.halildurmus.hotdeals.store;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
@Validated
public class StoreController {

  @Autowired
  private StoreService service;

  @PostMapping("/stores")
  public ResponseEntity<Store> saveStore(@Valid @RequestBody Store store) {
    final Store createdStore = service.saveStore(store);

    return ResponseEntity.status(201).body(createdStore);
  }

}
