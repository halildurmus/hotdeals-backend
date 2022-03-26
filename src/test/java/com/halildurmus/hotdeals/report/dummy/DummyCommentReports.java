package com.halildurmus.hotdeals.report.dummy;

import com.halildurmus.hotdeals.comment.dummy.DummyComments;
import com.halildurmus.hotdeals.report.comment.CommentReport;
import com.halildurmus.hotdeals.report.comment.CommentReportReason;
import com.halildurmus.hotdeals.user.dummy.DummyUsers;
import java.util.EnumSet;

public class DummyCommentReports {

  public static CommentReport commentReport1 = CommentReport.builder()
      .reportedBy(DummyUsers.user1)
      .reportedComment(DummyComments.comment1)
      .reasons(EnumSet.of(CommentReportReason.HARASSING, CommentReportReason.SPAM))
      .message("Lorem dolor sit amet").build();

}
