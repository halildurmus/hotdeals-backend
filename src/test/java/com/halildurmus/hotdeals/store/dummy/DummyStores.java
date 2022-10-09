package com.halildurmus.hotdeals.store.dummy;

import com.halildurmus.hotdeals.store.Store;

public class DummyStores {

  public static Store store1 =
      Store.builder()
          .id("5fbe790ec6f0b32014074bb1")
          .name("Amazon")
          .logo("http://www.gravatar.com/avatar")
          .build();

  public static Store store2 =
      Store.builder()
          .id("5fbe790ec6f0b32014074bb2")
          .name("Newegg")
          .logo("http://www.gravatar.com/avatar")
          .build();
}
