package com.halildurmus.hotdeals.store;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "stores", path = "stores")
public interface StoreRepository extends MongoRepository<Store, String> {

}