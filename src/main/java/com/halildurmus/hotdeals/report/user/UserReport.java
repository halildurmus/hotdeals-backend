package com.halildurmus.hotdeals.report.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.report.Report;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reports")
@TypeAlias("userReport")
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class UserReport extends Report {

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  @NotNull
  private ObjectId reportedUser;

}
