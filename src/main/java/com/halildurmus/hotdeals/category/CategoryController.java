package com.halildurmus.hotdeals.category;

import com.halildurmus.hotdeals.category.DTO.CategoryGetDTO;
import com.halildurmus.hotdeals.category.DTO.CategoryPostDTO;
import com.halildurmus.hotdeals.exception.CategoryNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.security.role.IsSuper;
import com.halildurmus.hotdeals.util.ObjectIdConstraint;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Api(tags = "categories")
@RestController
@RequestMapping("/categories")
@Validated
public class CategoryController {

  @Autowired
  private MapStructMapper mapStructMapper;

  @Autowired
  private CategoryService service;

  @GetMapping
  public List<CategoryGetDTO> getCategories(Pageable pageable) {
    final Page<Category> categories = service.findAll(pageable);

    return categories.getContent().stream()
        .map(mapStructMapper::categoryToCategoryGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  @IsSuper
  public CategoryGetDTO getCategory(@ObjectIdConstraint @PathVariable String id) {
    final Category category = service.findById(id).orElseThrow(CategoryNotFoundException::new);

    return mapStructMapper.categoryToCategoryGetDTO(category);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @IsSuper
  public CategoryGetDTO createCategory(@Valid @RequestBody CategoryPostDTO categoryPostDTO) {
    if (!categoryPostDTO.getNames().containsKey("en")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "The category name must have an English translation!");
    }
    final Category category = service.create(
        mapStructMapper.categoryPostDTOToCategory(categoryPostDTO));

    return mapStructMapper.categoryToCategoryGetDTO(category);
  }

  @PutMapping("/{id}")
  @IsSuper
  public CategoryGetDTO updateCategory(@ObjectIdConstraint @PathVariable String id,
      @Valid @RequestBody CategoryPostDTO categoryPostDTO) {
    final Category category = convertToEntity(id, categoryPostDTO);

    return mapStructMapper.categoryToCategoryGetDTO(service.update(category));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @IsSuper
  public void deleteCategory(@ObjectIdConstraint @PathVariable String id) {
    service.delete(id);
  }

  private Category convertToEntity(String id, CategoryPostDTO categoryPostDTO) {
    // Fetch the category from the db and set the missing properties from it
    final Category originalCategory = service.findById(id)
        .orElseThrow(CategoryNotFoundException::new);
    final Category category = mapStructMapper.categoryPostDTOToCategory(categoryPostDTO);
    category.setId(id);
    category.setCreatedAt(originalCategory.getCreatedAt());

    return category;
  }

}
