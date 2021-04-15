package com.halildurmus.hotdeals.store;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stores")
@TypeAlias("store")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Store {

  @Id
  private String id;

  @NotBlank
  private String name;

  @URL
  @NotNull
  private String logo;

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public Store() {}

  public Store(String name, String logo) {
    this.name = name;
    this.logo = logo;
  }

}
