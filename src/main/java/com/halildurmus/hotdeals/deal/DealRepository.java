package com.halildurmus.hotdeals.deal;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
          @CacheEvict(value = "deals:findAllByCategoryStartsWith", key = "#entity.category"),
          @CacheEvict(value = "deals:findAllByPostedBy", key = "#entity.postedBy"),
          @CacheEvict(value = "deals:findAllByStore", key = "#entity.store")
      })
  <S extends Deal> S save(S entity);

  @Cacheable(value = "deals:countDealsByStore", key = "#storeId", condition = "#storeId != null")
  int countDealsByStore(ObjectId storeId);

  @Cacheable(value = "deals:countDealsByPostedBy", key = "#postedBy", condition = "#postedBy != null")
  int countDealsByPostedBy(ObjectId postedBy);

  @Cacheable("deals:findAllByOrderByCreatedAtDesc")
  List<Deal> findAllByOrderByCreatedAtDesc();

  @Cacheable("deals:findAllByOrderByDealScoreDesc")
  List<Deal> findAllByOrderByDealScoreDesc();

  @Cacheable("deals:findAllByOrderByDiscountPrice")
  List<Deal> findAllByOrderByDiscountPrice();

  @Cacheable(value = "deals:findAllByCategoryStartsWith", key = "#category", condition = "#category.blank != true")
  List<Deal> findAllByCategoryStartsWith(String category);

  @Query("{ $or: [{\"title\" : { $regex: /.*?0.*/, $options: 'i'}}, {\"description\" : { $regex: /.*?0.*/, $options: 'i'}}] }")
  List<Deal> queryDeals(String keyword, Pageable pageable);

  @Cacheable(value = "deals:findAllByPostedBy", key = "#postedBy", condition = "#postedBy != null")
  List<Deal> findAllByPostedBy(ObjectId postedBy);

  @Cacheable(value = "deals:findAllByStore", key = "#storeId", condition = "#storeId != null")
  List<Deal> findAllByStore(ObjectId storeId);

}