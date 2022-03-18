package com.halildurmus.hotdeals.comment.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.user.dto.UserBasicDTO;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class CommentGetDTO {

  @Schema(description = "Comment ID", example = "5fbe790ec6f0b32014074bb1")
  private final String id;

  @Schema(description = "Deal ID", type = "String", example = "5fbe790ec6f0b32014074bb3")
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private final ObjectId dealId;

  @Schema(description = "Comment poster")
  private final UserBasicDTO postedBy;

  @Schema(description = "Comment message", example = "Thanks :)")
  private final String message;

  @Schema(description = "Comment createdAt", example = "2021-06-30T16:36:59.713Z")
  private final Instant createdAt;

}
