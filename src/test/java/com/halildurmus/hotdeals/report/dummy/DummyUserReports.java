package com.halildurmus.hotdeals.report.dummy;

import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.report.user.UserReportReason;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import java.util.EnumSet;

public class DummyUserReports {

  public static UserReport userReport1 =
      UserReport.builder()
          .reportedBy(DummyUsers.user2)
          .reportedUser(DummyUsers.user1)
          .reasons(EnumSet.of(UserReportReason.HARASSING, UserReportReason.OTHER))
          .message("Lorem dolor sit amet")
          .build();
}
