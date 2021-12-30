package com.halildurmus.hotdeals.category.DTO;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryGetDTO {

  private final String id;

  private final Map<String, String> names;

  private final String parent;

  private final String category;

  private final String iconLigature;

  private final String iconFontFamily;

}
