package com.halildurmus.hotdeals.category;

import com.halildurmus.hotdeals.security.role.IsSuper;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

  Page<Category> findAll(Pageable pageable);

  @IsSuper
  Optional<Category> findById(String id);

  @IsSuper
  Category create(Category category);

  @IsSuper
  Category update(Category category);

  @IsSuper
  void delete(String id);
}
