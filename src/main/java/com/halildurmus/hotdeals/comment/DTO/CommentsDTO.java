package com.halildurmus.hotdeals.comment.DTO;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentsDTO {

  private final long count;

  private final List<CommentGetDTO> comments;

}
