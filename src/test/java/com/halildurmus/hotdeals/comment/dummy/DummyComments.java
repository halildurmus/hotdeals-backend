package com.halildurmus.hotdeals.comment.dummy;

import com.halildurmus.hotdeals.comment.Comment;
import org.bson.types.ObjectId;

public class DummyComments {

  public static Comment comment1 = new Comment(new ObjectId("5fbe790ec6f0b32014074bb2"),
      "Lorem dolor sit amet");

}
