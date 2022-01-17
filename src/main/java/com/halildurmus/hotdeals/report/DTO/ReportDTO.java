package com.halildurmus.hotdeals.report.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("ReportDTO")
@Data
@SuperBuilder
@NoArgsConstructor
public class ReportDTO {

  @ApiModelProperty(value = "Report message", position = 1, example = "The user is harassing me")
  private String message;

}
