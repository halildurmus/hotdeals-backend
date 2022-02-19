package com.halildurmus.hotdeals.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.halildurmus.hotdeals.audit.DateAudit;
import com.halildurmus.hotdeals.user.User;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "reports")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class Report extends DateAudit implements Serializable {

  private static final long serialVersionUID = 1234567L;

  @Id
  private String id;

  @DocumentReference
  @JsonProperty(access = Access.READ_ONLY)
  private User reportedBy;

  private String message;

}
