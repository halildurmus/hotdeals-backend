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
          @CacheEvict(value = "comments:countCommentsByDealId", key = "#entity.dealId.toString()"),
          @CacheEvict(value = "comments:countCommentsByPostedById", key = "#entity.postedBy.id"),
          @CacheEvict(value = "comments:findByDealIdOrderByCreatedAt", allEntries = true)
      })
  <S extends Comment> S save(S entity);

  @Caching(evict = {
      @CacheEvict(value = "comments", allEntries = true),
      @CacheEvict(value = "comments:countCommentsByDealId", allEntries = true),
      @CacheEvict(value = "comments:countCommentsByPostedById", allEntries = true),
      @CacheEvict(value = "comments:findByDealIdOrderByCreatedAt", allEntries = true)
  })
  void deleteAllByIdIn(Iterable<String> ids);

  @Override
  @Caching(evict = {
      @CacheEvict(value = "comments", key = "#id"),
      @CacheEvict(value = "comments:countCommentsByDealId", allEntries = true),
      @CacheEvict(value = "comments:countCommentsByPostedById", allEntries = true),
      @CacheEvict(value = "comments:findByDealIdOrderByCreatedAt", allEntries = true)
  })
  void deleteById(String id);

  @Cacheable(value = "comments:countCommentsByDealId", key = "#dealId.toString()", condition = "#dealId != null")
  int countCommentsByDealId(ObjectId dealId);

  @Cacheable(value = "comments:countCommentsByPostedById", key = "#postedById.toString()", condition = "#postedById != null")
    // Parameter needs to be ObjectId
  int countCommentsByPostedById(ObjectId postedById);

  @Cacheable(value = "comments:findByDealIdOrderByCreatedAt", key = "T(java.lang.String).format('%s-%s', #dealId, #pageable)", condition = "#dealId != null")
  Page<Comment> findByDealIdOrderByCreatedAt(ObjectId dealId, Pageable pageable);

}