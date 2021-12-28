package com.halildurmus.hotdeals.comment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.user.UserGetDTO;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class CommentGetDTO {

  private final String message;
  
  private String id;

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId dealId;

  private UserGetDTO postedBy;

  private Instant createdAt;

}
