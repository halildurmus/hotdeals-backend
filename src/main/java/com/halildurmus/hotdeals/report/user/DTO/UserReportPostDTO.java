package com.halildurmus.hotdeals.report.user.DTO;

import com.halildurmus.hotdeals.report.DTO.ReportDTO;
import com.halildurmus.hotdeals.report.user.UserReportReason;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.EnumSet;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("UserReportPostDTO")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class UserReportPostDTO extends ReportDTO {

  @ApiModelProperty(value = "User report reasons", position = 1, example = "[HARASSING,OTHER]")
  @NotEmpty
  private EnumSet<UserReportReason> reasons;

}
