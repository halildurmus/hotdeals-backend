package com.halildurmus.hotdeals.store.dummy;

import com.halildurmus.hotdeals.store.Store;

public class DummyStores {

  public static Store store1 = Store.builder().name("Amazon")
      .logo("http://www.gravatar.com/avatar").build();

  public static Store store2 = Store.builder().name("Newegg")
      .logo("http://www.gravatar.com/avatar").build();

  public static Store storeWithId = Store.builder()
      .id("5fbe790ec6f0b32014074bb1")
      .name("Amazon")
      .logo("http://www.gravatar.com/avatar").build();

}
