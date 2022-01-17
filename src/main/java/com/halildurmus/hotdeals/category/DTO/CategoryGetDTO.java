package com.halildurmus.hotdeals.category.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@ApiModel("CategoryGetDTO")
@Data
@Builder
public class CategoryGetDTO {

  @ApiModelProperty(value = "Category id", position = 1, example = "5fbe790ec6f0b32014074bb1")
  private final String id;

  @ApiModelProperty(value = "Category names", position = 2, example = "{\"en\": \"Computers\", \"tr\": \"Bilgisayar\"}")
  private final Map<String, String> names;

  @ApiModelProperty(value = "Parent category path", position = 3, example = "/")
  private final String parent;

  @ApiModelProperty(value = "Category path", position = 4, example = "/computers")
  private final String category;

  @ApiModelProperty(value = "Category icon ligature", position = 5, example = "computer")
  private final String iconLigature;

  @ApiModelProperty(value = "Category icon font family", position = 6, example = "MaterialIcons")
  private final String iconFontFamily;

}
