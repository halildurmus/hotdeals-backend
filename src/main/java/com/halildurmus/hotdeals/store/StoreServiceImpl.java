package com.halildurmus.hotdeals.store;

import com.halildurmus.hotdeals.exception.StoreNotFoundException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StoreServiceImpl implements StoreService {

  @Autowired private StoreRepository repository;

  @Override
  public Page<Store> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Override
  public Optional<Store> findById(String id) {
    return repository.findById(id);
  }

  @Override
  public Store create(Store store) {
    return repository.save(store);
  }

  @Override
  public Store update(Store store) {
    repository.findById(store.getId()).orElseThrow(StoreNotFoundException::new);
    return repository.save(store);
  }

  @Override
  public void delete(String id) {
    repository.findById(id).orElseThrow(StoreNotFoundException::new);
    repository.deleteById(id);
  }
}
