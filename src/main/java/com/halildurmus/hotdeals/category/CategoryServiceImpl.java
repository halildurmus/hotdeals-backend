package com.halildurmus.hotdeals.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository repository;

  @Override
  public Category saveCategory(Category category) {
    if (!category.getParent().equals("/")) {
      repository.findByCategory(category.getParent())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "The parent category does not exists!"));
    }

    return repository.save(category);
  }

}
