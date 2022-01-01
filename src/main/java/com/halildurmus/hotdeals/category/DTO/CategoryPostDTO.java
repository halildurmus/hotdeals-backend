package com.halildurmus.hotdeals.category.DTO;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryPostDTO {

  @NotNull
  private final Map<String, String> names;

  @NotBlank
  private final String parent;

  @NotBlank
  private final String category;

  @NotBlank
  private final String iconLigature;

  @NotBlank
  private final String iconFontFamily;

}
