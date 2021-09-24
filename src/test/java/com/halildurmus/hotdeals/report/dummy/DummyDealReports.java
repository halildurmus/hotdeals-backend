package com.halildurmus.hotdeals.report.dummy;

import com.halildurmus.hotdeals.report.deal.DealReport;
import com.halildurmus.hotdeals.report.deal.DealReportReason;
import java.util.List;
import org.bson.types.ObjectId;

public class DummyDealReports {

  public static DealReport dealReport1 = DealReport.builder()
      .reportedDeal(new ObjectId("5fbe790ec6f0b32014074bb1"))
      .reasons(List.of(DealReportReason.REPOST, DealReportReason.SPAM))
      .message("Lorem dolor sit amet").build();

}
