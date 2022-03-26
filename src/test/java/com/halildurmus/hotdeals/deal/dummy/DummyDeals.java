package com.halildurmus.hotdeals.deal.dummy;

import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.deal.DealStatus;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import org.bson.types.ObjectId;

public class DummyDeals {

  public static Deal deal1 = Deal.builder()
      .id("5fbe720ec6f0b32014074bb0")
      .postedBy(new ObjectId("5fbe790ec6f0b32014074bb3"))
      .dealScore(0).views(0).status(DealStatus.ACTIVE).createdAt(Instant.now())
      .title("12TB WD My Book Desktop External HDD")
      .photos(List.of("http://www.gravatar.com/avatar"))
      .description("12TB WD External HDD\n$190").originalPrice(150.0).price(140.0)
      .store(new ObjectId("5fbe790ec6f0b32014074bb1")).category("/electronics")
      .coverPhoto("http://www.gravatar.com/avatar").dealUrl("https://www.amazon.com/item")
      .upvoters(new HashSet<>(List.of())).downvoters(new HashSet<>(List.of()))
      .createdAt(Instant.now()).updatedAt(Instant.now()).build();

}
