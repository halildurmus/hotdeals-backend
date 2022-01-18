package com.halildurmus.hotdeals.comment.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Schema(name = "CommentsDTO")
@Data
@Builder
public class CommentsDTO {

  @Schema(description = "Comment count", example = "5")
  private final long count;

  private final List<CommentGetDTO> comments;

}
