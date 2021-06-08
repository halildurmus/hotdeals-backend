package com.halildurmus.hotdeals.category;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "categories", path = "categories")
public interface CategoryRepository extends MongoRepository<Category, String> {

  List<Category> findAllByParent(String parent);

}