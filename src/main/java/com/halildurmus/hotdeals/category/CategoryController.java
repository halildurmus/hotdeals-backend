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
  public ResponseEntity<Object> saveOrUpdateCategory(@RequestBody Category category)
      throws Exception {
    final Category response = service.saveOrUpdateCategory(category);

    return ResponseEntity.status(201).body(response);
  }

}
