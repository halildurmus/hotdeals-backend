package com.halildurmus.hotdeals.comment.dummy;

import com.halildurmus.hotdeals.comment.Comment;
import org.bson.types.ObjectId;

public class DummyComments {

  public static Comment comment1 = Comment.builder()
      .dealId(new ObjectId("5fbe790ec6f0b32014074bb2"))
      .message("Lorem dolor sit amet").build();

}
