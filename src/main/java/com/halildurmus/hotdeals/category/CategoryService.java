package com.halildurmus.hotdeals.category;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

  Page<Category> findAll(Pageable pageable);

  Optional<Category> findById(String id);

  Category create(Category category);

  Category update(Category category);

  void delete(String id);

}
