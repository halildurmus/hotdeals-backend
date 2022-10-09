package com.halildurmus.hotdeals.report.comment.dto;

import com.halildurmus.hotdeals.report.comment.CommentReportReason;
import com.halildurmus.hotdeals.report.dto.ReportDTO;
import java.util.EnumSet;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class CommentReportPostDTO extends ReportDTO {

  @NotEmpty private EnumSet<CommentReportReason> reasons;
}
