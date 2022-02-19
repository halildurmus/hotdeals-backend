package com.halildurmus.hotdeals.report.deal;

import com.halildurmus.hotdeals.deal.Deal;
import com.halildurmus.hotdeals.report.Report;
import java.util.EnumSet;
import javax.validation.constraints.NotEmpty;
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

  private static final long serialVersionUID = 1234567L;

  @DocumentReference
  private Deal reportedDeal;

  @NotEmpty
  private EnumSet<DealReportReason> reasons;

}
