package com.halildurmus.hotdeals.deal;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "deals", path = "deals")
public interface DealRepository extends MongoRepository<Deal, String> {

  Optional<Deal> findByPostedBy(ObjectId postedBy);

}