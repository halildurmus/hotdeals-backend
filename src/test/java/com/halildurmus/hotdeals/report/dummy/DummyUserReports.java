package com.halildurmus.hotdeals.report.dummy;

import com.halildurmus.hotdeals.report.user.UserReport;
import com.halildurmus.hotdeals.report.user.UserReportReason;
import java.util.List;
import org.bson.types.ObjectId;

public class DummyUserReports {

  public static UserReport userReport1 = UserReport.builder()
      .reportedUser(new ObjectId("5fbe790ec6f0b32014074bb1"))
      .reasons(List.of(UserReportReason.HARASSING, UserReportReason.OTHER))
      .message("Lorem dolor sit amet").build();

}
