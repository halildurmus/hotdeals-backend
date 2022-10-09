package com.halildurmus.hotdeals.comment.dummy;

import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import java.time.Instant;
import org.bson.types.ObjectId;

public class DummyComments {

  public static Comment comment1 =
      Comment.builder()
          .id("5fbe790ec6f0b32214074bb5")
          .postedBy(DummyUsers.user1)
          .dealId(new ObjectId(DummyDeals.deal1.getId()))
          .message("Lorem dolor sit amet")
          .createdAt(Instant.now())
          .updatedAt(Instant.now())
          .build();
}
