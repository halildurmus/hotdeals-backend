package com.halildurmus.hotdeals.comment;

import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "comments", path = "comments")
public interface CommentRepository extends MongoRepository<Comment, String> {

  @Override
  @Caching(put = {@CachePut(value = "comments", key = "#entity.id")},
      evict = {
          @CacheEvict(value = "comments:countComments", key = "#entity.postedBy"),
          @CacheEvict(value = "comments:findByDealIdOrderByCreatedAtDesc", key = "#entity.dealId"),
          @CacheEvict(value = "deals:findAllByOrderByCreatedAtDesc", allEntries = true),
          @CacheEvict(value = "deals:findAllByOrderByDealScoreDesc", allEntries = true),
          @CacheEvict(value = "deals:findAllByOrderByDiscountPrice", allEntries = true)
      })
  <S extends Comment> S save(S entity);

  @Override
  @Caching(evict = {
      @CacheEvict(value = "comments", key = "#id"),
      @CacheEvict(value = "comments:countComments", allEntries = true),
      @CacheEvict(value = "comments:findByDealIdOrderByCreatedAtDesc", allEntries = true)
  })
  void deleteById(String id);

  @Cacheable(value = "comments:countComments", key = "#postedBy", condition = "#postedBy != null")
  int countCommentsByPostedBy(ObjectId postedBy);

  @Cacheable(value = "comments:findByDealIdOrderByCreatedAtDesc", key = "#dealId", condition = "#dealId != null")
  Optional<List<Comment>> findByDealIdOrderByCreatedAtDesc(ObjectId dealId);

}