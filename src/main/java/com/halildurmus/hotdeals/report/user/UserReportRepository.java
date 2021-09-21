package com.halildurmus.hotdeals.report.user;

import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "user-reports", path = "user-reports")
public interface UserReportRepository extends MongoRepository<UserReport, String> {

  @Override
  @CachePut(value = "userReports", key = "#entity.id")
  @CacheEvict(value = "userReports:findAll", allEntries = true)
  <S extends UserReport> S save(S entity);

  @Override
  @Caching(evict = {
      @CacheEvict(value = "userReports", key = "#id"),
      @CacheEvict(value = "userReports:findAll", allEntries = true)
  })
  void deleteById(String id);

  @Override
  @Cacheable(value = "userReports", key = "#id", condition = "#id.blank != true")
  Optional<UserReport> findById(String id);

  @Cacheable("userReports:findAll")
  Page<UserReport> findAllByReportedUserNotNull(Pageable pageable);

}
