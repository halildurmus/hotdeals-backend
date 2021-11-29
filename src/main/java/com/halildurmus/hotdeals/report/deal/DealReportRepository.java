package com.halildurmus.hotdeals.report.deal;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "deal-reports", path = "deal-reports")
public interface DealReportRepository extends MongoRepository<DealReport, String> {

  @Override
  @CachePut(value = "dealReports", key = "#entity.id")
  @CacheEvict(value = "dealReports:findAll", allEntries = true)
  <S extends DealReport> S save(S entity);

  @Override
  @Caching(evict = {
      @CacheEvict(value = "dealReports", key = "#id"),
      @CacheEvict(value = "dealReports:findAll", allEntries = true)
  })
  void deleteById(String id);

  @Override
  @Cacheable(value = "dealReports", key = "#id", condition = "#id.blank != true and #result != null")
  Optional<DealReport> findById(String id);

  @Override
  @Cacheable("dealReports:findAll")
  @Query("{\"reportedDeal\" : { $exists: true } }")
  Page<DealReport> findAll(Pageable pageable);

}