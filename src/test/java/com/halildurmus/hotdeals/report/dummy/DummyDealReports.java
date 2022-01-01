package com.halildurmus.hotdeals.report.dummy;

import com.halildurmus.hotdeals.deal.dummy.DummyDeals;
import com.halildurmus.hotdeals.report.deal.DealReport;
import com.halildurmus.hotdeals.report.deal.DealReportReason;
import java.util.EnumSet;

public class DummyDealReports {

  public static DealReport dealReport1 = DealReport.builder()
      .reportedDeal(DummyDeals.deal1)
      .reasons(EnumSet.of(DealReportReason.REPOST, DealReportReason.SPAM))
      .message("Lorem dolor sit amet").build();

}
