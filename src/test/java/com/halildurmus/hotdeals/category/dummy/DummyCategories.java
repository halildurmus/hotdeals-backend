package com.halildurmus.hotdeals.category.dummy;

import com.halildurmus.hotdeals.category.Category;
import java.util.Map;

public class DummyCategories {

  public static Category category1 = Category.builder().id("5fbe790ec6f0b32014074bb1")
      .names(Map.of("en", "Computers", "tr", "Bilgisayar"))
      .parent("/").category("/computers").iconLigature("computer")
      .iconFontFamily("MaterialIcons").build();

  public static Category category2 = Category.builder().id("5fbe790ec6f0b32014074bb2")
      .names(Map.of("en", "Electronics", "tr", "Elektronik"))
      .parent("/").category("/electronics").iconLigature("devices")
      .iconFontFamily("MaterialIcons").build();

  public static Category category1WithoutEnglishTranslation = Category.builder()
      .names(Map.of("tr", "CPU"))
      .parent("/computers").category("/computers/cpus").iconLigature("devices")
      .iconFontFamily("MaterialIcons").build();

}
