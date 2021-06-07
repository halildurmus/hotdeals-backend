package com.halildurmus.hotdeals.deal;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "deals", path = "deals")
public interface DealRepository extends MongoRepository<Deal, String> {

  int countDealsByStore(ObjectId storeId);

  int countDealsByPostedBy(ObjectId postedBy);

  List<Deal> findAllByOrderByCreatedAtDesc();

  List<Deal> findAllByOrderByDealScoreDesc();

  // List<Deal> findAllByOrderByPrice();

  List<Deal> findAllByCategoryStartsWith(String category);

  List<Deal> findAllByPostedBy(ObjectId postedBy);

  List<Deal> findAllByStore(ObjectId storeId);

}