package com.halildurmus.hotdeals.report.comment;

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

@RepositoryRestResource(collectionResourceRel = "comment-reports", path = "comment-reports")
public interface CommentReportRepository extends MongoRepository<CommentReport, String> {

  @Override
  @CachePut(value = "commentReports", key = "#entity.id")
  @CacheEvict(value = "commentReports:findAll", allEntries = true)
  <S extends CommentReport> S save(S entity);

  @Override
  @Caching(evict = {
      @CacheEvict(value = "commentReports", key = "#id"),
      @CacheEvict(value = "commentReports:findAll", allEntries = true)
  })
  void deleteById(String id);

  @Override
  @Cacheable(value = "commentReports", key = "#id", condition = "#id.blank != true and #result != null")
  Optional<CommentReport> findById(String id);

  @Override
  @Cacheable("commentReports:findAll")
  @Query("{\"reportedComment\" : { $exists: true } }")
  Page<CommentReport> findAll(Pageable pageable);

}