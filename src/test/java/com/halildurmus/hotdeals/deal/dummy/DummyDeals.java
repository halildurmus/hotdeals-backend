package com.halildurmus.hotdeals.deal.dummy;

import com.halildurmus.hotdeals.deal.Deal;
import org.bson.types.ObjectId;

public class DummyDeals {

  public static Deal deal1 = Deal.builder().title("12TB WD My Book Desktop External HDD")
      .description("12TB WD External HDD\n$190").originalPrice(150.0).price(140.0)
      .store(new ObjectId("5fbe790ec6f0b32014074bb1")).category("/electronics")
      .coverPhoto("http://www.gravatar.com/avatar").dealUrl("https://www.amazon.com/item").build();

}
