package com.halildurmus.hotdeals.store;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "stores", path = "stores")
public interface StoreRepository extends MongoRepository<Store, String> {

  @Override
  @CachePut(value = "stores", key = "#entity.id")
  @CacheEvict("stores:findAll")
  <S extends Store> S save(S entity);

  @Override
  @Caching(evict = {
      @CacheEvict(value = "stores", key = "#id"),
      @CacheEvict("stores:findAll")
  })
  void deleteById(String id);

  @Override
  @Cacheable("stores:findAll")
  Page<Store> findAll(Pageable pageable);

}