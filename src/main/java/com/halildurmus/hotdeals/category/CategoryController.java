package com.halildurmus.hotdeals.category;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RepositoryRestController
@Validated
public class CategoryController {

  @Autowired
  private CategoryService service;

  @PostMapping("/categories")
  public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category)
      throws Exception {
    if (!category.getNames().containsKey("en")) {
      throw new Exception("The category name must have an English translation!");
    }

    final Category savedCategory = service.saveCategory(category);

    return ResponseEntity.status(201).body(savedCategory);
  }

}
