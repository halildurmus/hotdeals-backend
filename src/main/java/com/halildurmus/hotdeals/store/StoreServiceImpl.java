package com.halildurmus.hotdeals.store;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StoreServiceImpl implements StoreService {

  @Autowired
  private StoreRepository repository;

  @Override
  public Store save(Store store) {
    return repository.save(store);
  }

}
