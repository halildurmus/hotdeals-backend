package com.halildurmus.hotdeals.category.dummy;

import com.halildurmus.hotdeals.category.Category;
import java.util.Map;

public class DummyCategories {

  public static Category category1 = Category.builder()
      .names(Map.of("en", "Computers", "tr", "Bilgisayarlar"))
      .parent("/").category("/computers").iconLigature("computer")
      .iconFontFamily("MaterialIcons").build();

}
