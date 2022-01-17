package com.halildurmus.hotdeals.category.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@ApiModel("CategoryPostDTO")
@Data
@Builder
public class CategoryPostDTO {

  @ApiModelProperty(value = "Category names", position = 1, example = "{\"en\": \"Computers\", \"tr\": \"Bilgisayar\"}", required = true)
  @NotNull
  private final Map<String, String> names;

  @ApiModelProperty(value = "Parent category path", position = 2, example = "/", required = true)
  @NotBlank
  private final String parent;

  @ApiModelProperty(value = "Category path", position = 3, example = "/computers", required = true)
  @NotBlank
  private final String category;

  @ApiModelProperty(value = "Category icon ligature", position = 4, example = "computer", required = true)
  @NotBlank
  private final String iconLigature;

  @ApiModelProperty(value = "Category icon font family", position = 5, example = "MaterialIcons", required = true)
  @NotBlank
  private final String iconFontFamily;

}
