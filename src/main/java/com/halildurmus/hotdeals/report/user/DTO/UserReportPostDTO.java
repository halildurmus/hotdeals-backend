package com.halildurmus.hotdeals.report.user.DTO;

import com.halildurmus.hotdeals.report.DTO.ReportDTO;
import com.halildurmus.hotdeals.report.user.UserReportReason;
import com.halildurmus.hotdeals.user.User;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class UserReportPostDTO extends ReportDTO {

  @NotNull
  private User reportedUser;

  @NotEmpty
  private List<UserReportReason> reasons;

}
