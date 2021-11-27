package com.halildurmus.hotdeals.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository repository;

  @Override
  public Category saveCategory(Category category) throws Exception {
    if (!category.getParent().equals("/")) {
      repository.findByCategory(category.getParent())
          .orElseThrow(() -> new Exception("The category's parent category does not exists!"));
    }

    return repository.save(category);
  }

}
