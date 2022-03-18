package com.halildurmus.hotdeals.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryGetDTO {

  @Schema(description = "Category ID", example = "5fbe790ec6f0b32014074bb1")
  private final String id;

  @Schema(description = "Category names", example = "{\"en\": \"Computers\", \"tr\": \"Bilgisayar\"}")
  private final Map<String, String> names;

  @Schema(description = "Parent category path", example = "/")
  private final String parent;

  @Schema(description = "Category path", example = "/computers")
  private final String category;

  @Schema(description = "Category icon ligature", example = "computer")
  private final String iconLigature;

  @Schema(description = "Category icon font family", example = "MaterialIcons")
  private final String iconFontFamily;

}
