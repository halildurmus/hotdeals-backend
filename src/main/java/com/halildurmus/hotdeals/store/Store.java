package com.halildurmus.hotdeals.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stores")
@TypeAlias("store")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store implements Serializable {

  private static final long serialVersionUID = 1234567L;

  @Id
  private String id;

  @NotBlank
  private String name;

  @URL
  @NotNull
  private String logo;

  @CreatedDate
  // See https://github.com/spring-projects/spring-data-rest/issues/1565
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant createdAt;

  @LastModifiedDate
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant updatedAt;

}
