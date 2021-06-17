package com.halildurmus.hotdeals.category;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
public interface CategoryRepository extends MongoRepository<Category, String> {

  @Override
  @CachePut(value = "categories", key = "#entity.id")
  @CacheEvict("categories:findAll")
  <S extends Category> S save(S entity);

  @Override
  @Caching(evict = {
      @CacheEvict(value = "categories", key = "#id"),
      @CacheEvict("categories:findAll")
  })
  void deleteById(String id);

  @Override
  @Cacheable(value = "categories", key = "#id", condition = "#id.blank != true")
  Optional<Category> findById(String id);

  @Override
  @Cacheable("categories:findAll")
  Page<Category> findAll(Pageable pageable);
}