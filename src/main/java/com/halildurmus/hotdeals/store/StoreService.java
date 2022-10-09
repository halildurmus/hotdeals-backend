package com.halildurmus.hotdeals.store;

import com.halildurmus.hotdeals.security.role.IsSuper;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreService {

  Page<Store> findAll(Pageable pageable);

  @IsSuper
  Optional<Store> findById(String id);

  @IsSuper
  Store create(Store store);

  @IsSuper
  Store update(Store store);

  @IsSuper
  void delete(String id);
}
