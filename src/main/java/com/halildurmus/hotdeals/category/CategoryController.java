package com.halildurmus.hotdeals.category;

import com.halildurmus.hotdeals.category.DTO.CategoryGetDTO;
import com.halildurmus.hotdeals.category.DTO.CategoryPostDTO;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@RepositoryRestController
@Validated
public class CategoryController {

  @Autowired
  private MapStructMapper mapStructMapper;

  @Autowired
  private CategoryService service;

  @GetMapping("/categories")
  public ResponseEntity<List<CategoryGetDTO>> getCategories(Pageable pageable) {
    final Page<Category> categories = service.findAll(pageable);
    final List<CategoryGetDTO> categoryGetDTOs = categories.getContent().stream()
        .map(category -> mapStructMapper.categoryToCategoryGetDTO(category)).collect(
            Collectors.toList());

    return ResponseEntity.ok(categoryGetDTOs);
  }

  @GetMapping("/categories/{id}")
  public ResponseEntity<CategoryGetDTO> getCategory(@ObjectIdConstraint @PathVariable String id) {
    final Optional<Category> category = service.findById(id);
    if (category.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok().body(mapStructMapper.categoryToCategoryGetDTO(category.get()));
  }

  @PostMapping("/categories")
  public ResponseEntity<CategoryGetDTO> createCategory(
      @Valid @RequestBody CategoryPostDTO categoryPostDTO) {
    if (!categoryPostDTO.getNames().containsKey("en")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The category name must have an English translation!");
    }
    final Category category = service.save(
        mapStructMapper.categoryPostDTOCategory(categoryPostDTO));

    return ResponseEntity.status(201).body(mapStructMapper.categoryToCategoryGetDTO(category));
  }

}
