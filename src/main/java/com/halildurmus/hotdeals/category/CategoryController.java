package com.halildurmus.hotdeals.category;

import com.halildurmus.hotdeals.category.dto.CategoryGetDTO;
import com.halildurmus.hotdeals.category.dto.CategoryPostDTO;
import com.halildurmus.hotdeals.exception.CategoryNotFoundException;
import com.halildurmus.hotdeals.mapstruct.MapStructMapper;
import com.halildurmus.hotdeals.security.role.IsSuper;
import com.halildurmus.hotdeals.util.IsObjectId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springdoc.api.annotations.ParameterObject;
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

@Tag(name = "categories")
@RestController
@RequestMapping("/categories")
@Validated
public class CategoryController {

  @Autowired private MapStructMapper mapStructMapper;

  @Autowired private CategoryService service;

  @GetMapping
  @Operation(summary = "Returns all categories")
  @ApiResponses(
      @ApiResponse(
          responseCode = "200",
          description = "Successful operation",
          content =
              @Content(
                  mediaType = "application/json",
                  array = @ArraySchema(schema = @Schema(implementation = CategoryGetDTO.class)))))
  public List<CategoryGetDTO> getCategories(@ParameterObject Pageable pageable) {
    final Page<Category> categories = service.findAll(pageable);

    return categories.getContent().stream()
        .map(mapStructMapper::categoryToCategoryGetDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  @IsSuper
  @Operation(summary = "Finds category by ID", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Successful operation",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CategoryGetDTO.class))),
    @ApiResponse(responseCode = "400", description = "Invalid category ID", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
  })
  public CategoryGetDTO getCategory(
      @Parameter(
              description = "String representation of the Category ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id) {
    final Category category = service.findById(id).orElseThrow(CategoryNotFoundException::new);

    return mapStructMapper.categoryToCategoryGetDTO(category);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @IsSuper
  @Operation(summary = "Creates a category", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "The category created successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CategoryGetDTO.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
  })
  public CategoryGetDTO createCategory(@Valid @RequestBody CategoryPostDTO categoryPostDTO) {
    if (!categoryPostDTO.getNames().containsKey("en")) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "The category name must have an English translation!");
    }
    final Category category =
        service.create(mapStructMapper.categoryPostDTOToCategory(categoryPostDTO));

    return mapStructMapper.categoryToCategoryGetDTO(category);
  }

  @PutMapping("/{id}")
  @IsSuper
  @Operation(
      summary = "Updates an existing category",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "The category successfully updated",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CategoryGetDTO.class))),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
  })
  public CategoryGetDTO updateCategory(
      @Parameter(
              description = "String representation of the Category ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id,
      @Valid @RequestBody CategoryPostDTO categoryPostDTO) {
    final Category category = convertToEntity(id, categoryPostDTO);

    return mapStructMapper.categoryToCategoryGetDTO(service.update(category));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @IsSuper
  @Operation(
      summary = "Deletes an existing category",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses({
    @ApiResponse(
        responseCode = "204",
        description = "The category successfully deleted",
        content = @Content),
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
  })
  public void deleteCategory(
      @Parameter(
              description = "String representation of the Category ID",
              example = "5fbe790ec6f0b32014074bb1")
          @IsObjectId
          @PathVariable
          String id) {
    service.delete(id);
  }

  private Category convertToEntity(String id, CategoryPostDTO categoryPostDTO) {
    // Fetch the category from the db and set the missing properties from it
    final Category originalCategory =
        service.findById(id).orElseThrow(CategoryNotFoundException::new);
    final Category category = mapStructMapper.categoryPostDTOToCategory(categoryPostDTO);
    category.setId(id);
    category.setCreatedAt(originalCategory.getCreatedAt());

    return category;
  }
}
