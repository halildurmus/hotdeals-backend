package com.halildurmus.hotdeals.report.deal.DTO;

import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.report.DTO.ReportDTO;
import com.halildurmus.hotdeals.report.deal.DealReportReason;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class DealReportPostDTO extends ReportDTO {

  @NotNull
  private Deal reportedDeal;

  @NotEmpty
  private List<DealReportReason> reasons;

}
