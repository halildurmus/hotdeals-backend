package com.halildurmus.hotdeals.comment.dummy;

import com.halildurmus.hotdeals.comment.Comment;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import java.time.Instant;
import org.bson.types.ObjectId;

public class DummyComments {

  public static Comment comment1 = Comment.builder()
      .id("5fbe790ec6f0b32214074bb1")
      .postedBy(DummyUsers.user1)
      .dealId(new ObjectId("5fbe790ec6f0b32014074bb2"))
      .message("Lorem dolor sit amet")
      .createdAt(Instant.now()).build();

}
