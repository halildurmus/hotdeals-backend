package com.halildurmus.hotdeals.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.halildurmus.hotdeals.user.User;
import java.io.Serializable;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "reports")
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class Report implements Serializable {

  private static final long serialVersionUID = 1234567L;

  @Id
  private String id;

  @DocumentReference
  @JsonProperty(access = Access.READ_ONLY)
  private User reportedBy;

  private String message;

  @CreatedDate
  // See https://github.com/spring-projects/spring-data-rest/issues/1565
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant createdAt;

  @LastModifiedDate
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant updatedAt;

}
