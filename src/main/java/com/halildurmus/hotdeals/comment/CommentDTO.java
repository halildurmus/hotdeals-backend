package com.halildurmus.hotdeals.comment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.user.UserDTO;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import java.io.Serializable;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class CommentDTO implements Serializable {

  private static final long serialVersionUID = 1234567L;

  private String id;

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId dealId;

  private UserDTO postedBy;

  private String message;

  private Instant createdAt;

}
