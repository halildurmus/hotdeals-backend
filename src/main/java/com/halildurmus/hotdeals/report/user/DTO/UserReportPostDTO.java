package com.halildurmus.hotdeals.report.user.DTO;

import com.halildurmus.hotdeals.report.DTO.ReportDTO;
import com.halildurmus.hotdeals.report.user.UserReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.EnumSet;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Schema(name = "UserReportPostDTO")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class UserReportPostDTO extends ReportDTO {

  @Schema(description = "User report reasons", example = "[HARASSING,OTHER]")
  @NotEmpty
  private EnumSet<UserReportReason> reasons;

}
