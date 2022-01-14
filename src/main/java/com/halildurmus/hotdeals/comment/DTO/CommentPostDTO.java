package com.halildurmus.hotdeals.comment.DTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPostDTO {

  @NotBlank
  @Size(min = 1, max = 500)
  private String message;

}
