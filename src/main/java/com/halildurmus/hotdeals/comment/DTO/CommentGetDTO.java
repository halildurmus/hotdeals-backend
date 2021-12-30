package com.halildurmus.hotdeals.comment.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.user.DTO.UserGetDTO;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class CommentGetDTO {

  private final String id;

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private final ObjectId dealId;

  private final UserGetDTO postedBy;

  private final String message;

  private final Instant createdAt;

}
