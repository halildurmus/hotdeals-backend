package com.halildurmus.hotdeals.comment;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentsDTO {

  private long count;

  private List<CommentGetDTO> comments;

}
