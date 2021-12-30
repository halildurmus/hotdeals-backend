package com.halildurmus.hotdeals.category;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryRepository repository;

  @Override
  public Page<Category> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Override
  public Optional<Category> findById(String id) {
    return repository.findById(id);
  }

  @Override
  public Category save(Category category) {
    if (!category.getParent().equals("/")) {
      repository.findByCategory(category.getParent())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
              "The parent category does not exists!"));
    }

    return repository.save(category);
  }

}
