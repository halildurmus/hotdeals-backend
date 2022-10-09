package com.halildurmus.hotdeals.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentsDTO {

  @Schema(description = "Comment count", example = "5")
  private final long count;

  private final List<CommentGetDTO> comments;
}
