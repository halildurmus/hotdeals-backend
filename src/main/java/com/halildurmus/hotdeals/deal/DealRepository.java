package com.halildurmus.hotdeals.deal;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "deals", path = "deals")
public interface DealRepository extends MongoRepository<Deal, String> {

  @Override
  @Caching(put = {@CachePut(value = "deals", key = "#entity.id")},
      evict = {
          @CacheEvict(value = "deals:countDealsByStore", key = "#entity.store"),
          @CacheEvict(value = "deals:countDealsByPostedBy", key = "#entity.postedBy"),
          @CacheEvict(value = "deals:findAllByIsExpiredIsFalseOrderByCreatedAtDesc", allEntries = true),
          @CacheEvict(value = "deals:findAllByIsExpiredIsFalseOrderByDealScoreDesc", allEntries = true),
          @CacheEvict(value = "deals:findAllByPostedByOrderByCreatedAtDesc", allEntries = true),
      })
  <S extends Deal> S save(S entity);

  @Cacheable(value = "deals:countDealsByStore", key = "#storeId", condition = "#storeId != null and #result != null")
  int countDealsByStore(ObjectId storeId);

  @Cacheable(value = "deals:countDealsByPostedBy", key = "#postedBy", condition = "#postedBy != null and #result != null")
  int countDealsByPostedBy(ObjectId postedBy);

  Page<Deal> findAllByIdIn(Iterable<String> ids, Pageable pageable);

  @Cacheable("deals:findAllByIsExpiredIsFalseOrderByCreatedAtDesc")
  Page<Deal> findAllByIsExpiredIsFalseOrderByCreatedAtDesc(Pageable pageable);

  @Cacheable("deals:findAllByIsExpiredIsFalseOrderByDealScoreDesc")
  Page<Deal> findAllByIsExpiredIsFalseOrderByDealScoreDesc(Pageable pageable);

  @Cacheable(value = "deals:findAllByPostedByOrderByCreatedAtDesc", key = "T(java.lang.String).format('%s-%s', #postedBy, #pageable)", condition = "#postedBy != null and #result != null")
  Page<Deal> findAllByPostedByOrderByCreatedAtDesc(ObjectId postedBy, Pageable pageable);

}