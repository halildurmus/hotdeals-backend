package com.halildurmus.hotdeals.category.dummy;

import com.halildurmus.hotdeals.category.Category;
import java.util.Map;

public class DummyCategories {

  public static Category category1 = new Category(
      Map.of("en", "Computers", "tr", "Bilgisayarlar"),
      "/", "/computers",
      "computer", "MaterialIcons");

}
