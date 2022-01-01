package com.halildurmus.hotdeals.report.deal.DTO;

import com.halildurmus.hotdeals.report.DTO.ReportDTO;
import com.halildurmus.hotdeals.report.deal.DealReportReason;
import java.util.EnumSet;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class DealReportPostDTO extends ReportDTO {

  @NotEmpty
  private EnumSet<DealReportReason> reasons;

}
