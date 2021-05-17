package com.halildurmus.hotdeals.deal;

import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "deals", path = "deals")
public interface DealRepository extends MongoRepository<Deal, String> {

  List<Deal> findAllByOrderByCreatedAtDesc();

  List<Deal> findAllByOrderByLikesDesc();

 // List<Deal> findAllByOrderByPrice();

  List<Deal> findAllByPostedBy(ObjectId postedBy);

}