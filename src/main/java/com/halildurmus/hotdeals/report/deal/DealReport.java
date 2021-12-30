package com.halildurmus.hotdeals.report.deal;

import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.report.Report;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "reports")
@TypeAlias("dealReport")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class DealReport extends Report {

  @NotNull
  @DocumentReference
  private Deal reportedDeal;

  @NotEmpty
  private List<DealReportReason> reasons;

}
