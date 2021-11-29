package com.halildurmus.hotdeals.deal;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "deals", path = "deals")
public interface DealRepository extends MongoRepository<Deal, String> {

  @Override
  @Caching(put = {@CachePut(value = "deals", key = "#entity.id")},
      evict = {
          @CacheEvict(value = "deals:countDealsByStore", key = "#entity.store"),
          @CacheEvict(value = "deals:countDealsByPostedBy", key = "#entity.postedBy"),
          @CacheEvict(value = "deals:findAllByOrderByCreatedAtDesc", allEntries = true),
          @CacheEvict(value = "deals:findAllByOrderByDealScoreDesc", allEntries = true),
          @CacheEvict(value = "deals:findAllByOrderByDiscountPrice", allEntries = true),
          @CacheEvict(value = "deals:findAllByCategoryStartsWith", allEntries = true),
          @CacheEvict(value = "deals:findAllByPostedByOrderByCreatedAtDesc", allEntries = true),
          @CacheEvict(value = "deals:findAllByStore", allEntries = true)
      })
  <S extends Deal> S save(S entity);

  @Cacheable(value = "deals:countDealsByStore", key = "#storeId", condition = "#storeId != null and #result != null")
  int countDealsByStore(ObjectId storeId);

  @Cacheable(value = "deals:countDealsByPostedBy", key = "#postedBy", condition = "#postedBy != null and #result != null")
  int countDealsByPostedBy(ObjectId postedBy);

  Page<Deal> findAllByIdIn(Iterable<String> ids, Pageable pageable);

  @Cacheable("deals:findAllByOrderByCreatedAtDesc")
  Page<Deal> findAllByOrderByCreatedAtDesc(Pageable pageable);

  @Cacheable("deals:findAllByOrderByDealScoreDesc")
  Page<Deal> findAllByOrderByDealScoreDesc(Pageable pageable);

  @Cacheable("deals:findAllByOrderByDiscountPrice")
  Page<Deal> findAllByOrderByDiscountPrice(Pageable pageable);

  @Cacheable(value = "deals:findAllByCategoryStartsWith", key = "T(java.lang.String).format('%s-%s', #category, #pageable)", condition = "#category.blank != true and #result != null")
  Page<Deal> findAllByCategoryStartsWith(String category, Pageable pageable);

  @Query("{ $or: [{\"title\" : { $regex: /.*?0.*/, $options: 'i'}}, {\"description\" : { $regex: /.*?0.*/, $options: 'i'}}] }")
  Page<Deal> queryDeals(String keyword, Pageable pageable);

  @Cacheable(value = "deals:findAllByPostedByOrderByCreatedAtDesc", key = "T(java.lang.String).format('%s-%s', #postedBy, #pageable)", condition = "#postedBy != null and #result != null")
  Page<Deal> findAllByPostedByOrderByCreatedAtDesc(ObjectId postedBy, Pageable pageable);

  @Cacheable(value = "deals:findAllByStore", key = "T(java.lang.String).format('%s-%s', #storeId, #pageable)", condition = "#storeId != null and #result != null")
  Page<Deal> findAllByStore(ObjectId storeId, Pageable pageable);

}