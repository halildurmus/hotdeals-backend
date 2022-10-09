package com.halildurmus.hotdeals.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class DateAudit implements Serializable {

  private static final long serialVersionUID = 1L;

  @Schema(description = "createdAt", example = "2021-06-30T16:36:59.713Z")
  @CreatedDate
  // See https://github.com/spring-projects/spring-data-rest/issues/1565
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant createdAt;

  @Schema(description = "updatedAt", example = "2021-06-30T16:36:59.713Z")
  @LastModifiedDate
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant updatedAt;
}
