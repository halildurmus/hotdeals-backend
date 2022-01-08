package com.halildurmus.hotdeals.store;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {

  Page<Store> findAll(Pageable pageable);

  Optional<Store> findById(String id);

  Store create(Store store);

  Store update(Store store);

  void delete(String id);

}
