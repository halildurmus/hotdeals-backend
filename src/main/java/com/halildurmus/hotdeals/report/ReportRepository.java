package com.halildurmus.hotdeals.report;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "reports", path = "reports")
public interface ReportRepository extends MongoRepository<Report, String> {

  @Override
  @CachePut(value = "reports", key = "#entity.id")
  @CacheEvict(value = "reports:findAll", allEntries = true)
  <S extends Report> S save(S entity);

  @Override
  @Caching(evict = {
      @CacheEvict(value = "reports", key = "#id"),
      @CacheEvict(value = "reports:findAll", allEntries = true)
  })
  void deleteById(String id);

  @Override
  @Cacheable(value = "reports", key = "#id", condition = "#id.blank != true")
  Optional<Report> findById(String id);

  @Override
  @Cacheable("reports:findAll")
  Page<Report> findAll(Pageable pageable);

}