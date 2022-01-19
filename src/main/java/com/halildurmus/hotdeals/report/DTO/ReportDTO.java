package com.halildurmus.hotdeals.report.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class ReportDTO {

  @Schema(description = "Report message", example = "The user is harassing me")
  private String message;

}
