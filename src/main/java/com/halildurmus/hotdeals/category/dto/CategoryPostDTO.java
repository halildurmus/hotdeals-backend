package com.halildurmus.hotdeals.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryPostDTO {

  @Schema(
      description = "Category names",
      example = "{\"en\": \"Computers\", \"tr\": \"Bilgisayar\"}")
  @NotNull
  private final Map<String, String> names;

  @Schema(description = "Parent category path", example = "/")
  @NotBlank
  private final String parent;

  @Schema(description = "Category path", example = "/computers")
  @NotBlank
  private final String category;

  @Schema(description = "Category icon ligature", example = "computer")
  @NotBlank
  private final String iconLigature;

  @Schema(description = "Category icon font family", example = "MaterialIcons")
  @NotBlank
  private final String iconFontFamily;
}
