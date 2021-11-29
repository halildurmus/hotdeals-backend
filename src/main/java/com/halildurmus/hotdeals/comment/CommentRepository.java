package com.halildurmus.hotdeals.comment;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "comments", path = "comments")
public interface CommentRepository extends MongoRepository<Comment, String> {

  @Override
  @Caching(put = {@CachePut(value = "comments", key = "#entity.id")},
      evict = {
          @CacheEvict(value = "comments:countCommentsByDealId", key = "#entity.dealId"),
          @CacheEvict(value = "comments:countCommentsByPostedBy", key = "#entity.postedBy"),
          @CacheEvict(value = "comments:findByDealIdOrderByCreatedAt", allEntries = true),
          @CacheEvict(value = "deals:findAllByOrderByCreatedAtDesc", allEntries = true),
          @CacheEvict(value = "deals:findAllByOrderByDealScoreDesc", allEntries = true),
          @CacheEvict(value = "deals:findAllByOrderByDiscountPrice", allEntries = true)
      })
  <S extends Comment> S save(S entity);

  @Override
  @Caching(evict = {
      @CacheEvict(value = "comments", key = "#id"),
      @CacheEvict(value = "comments:countCommentsByDealId", allEntries = true),
      @CacheEvict(value = "comments:countCommentsByPostedBy", allEntries = true),
      @CacheEvict(value = "comments:findByDealIdOrderByCreatedAt", allEntries = true)
  })
  void deleteById(String id);

  @Cacheable(value = "comments:countCommentsByDealId", key = "#dealId", condition = "#dealId != null and #result != null")
  int countCommentsByDealId(ObjectId dealId);

  @Cacheable(value = "comments:countCommentsByPostedBy", key = "#postedBy", condition = "#postedBy != null and #result != null")
  int countCommentsByPostedBy(ObjectId postedBy);

  @Cacheable(value = "comments:findByDealIdOrderByCreatedAt", key = "T(java.lang.String).format('%s-%s', #dealId, #pageable)", condition = "#dealId != null and #result != null")
  Page<Comment> findByDealIdOrderByCreatedAt(ObjectId dealId, Pageable pageable);

}