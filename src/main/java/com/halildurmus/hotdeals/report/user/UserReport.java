package com.halildurmus.hotdeals.report.user;

import com.halildurmus.hotdeals.report.Report;
import com.halildurmus.hotdeals.user.User;
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
@TypeAlias("userReport")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class UserReport extends Report {

  @NotNull
  @DocumentReference(lazy = true)
  private User reportedUser;

  @NotEmpty
  private List<UserReportReason> reasons;

}
