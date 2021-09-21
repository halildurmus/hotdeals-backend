package com.halildurmus.hotdeals.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
public class CategoryController {

  @Autowired
  private CategoryService service;

  @PostMapping("/categories")
  public ResponseEntity<Category> saveOrUpdateCategory(@RequestBody Category category)
      throws Exception {
    if (!category.getNames().containsKey("en")) {
      throw new Exception("The category name must have an English translation!");
    }

    final Category response = service.saveOrUpdateCategory(category);

    return ResponseEntity.status(201).body(response);
  }

}
