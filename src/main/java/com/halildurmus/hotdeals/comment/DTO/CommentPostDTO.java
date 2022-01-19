package com.halildurmus.hotdeals.comment.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
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

  @Schema(description = "Comment message", example = "Thanks :)")
  @NotBlank
  @Size(min = 1, max = 500)
  private String message;

}
