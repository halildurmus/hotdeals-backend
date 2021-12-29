package com.halildurmus.hotdeals.category;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@RepositoryRestController
@Validated
public class CategoryController {

  @Autowired
  private CategoryService service;

  @PostMapping("/categories")
  public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
    if (!category.getNames().containsKey("en")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The category name must have an English translation!");
    }

    return ResponseEntity.status(201).body(service.saveCategory(category));
  }

}
