package com.halildurmus.hotdeals.report;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reports")
@Data
@NoArgsConstructor
@SuperBuilder
public abstract class Report implements Serializable {

  private static final long serialVersionUID = 1234567L;

  @Id
  private String id;

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId reportedBy;

  @NotEmpty
  private List<String> reasons;

  private String message;

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

}
