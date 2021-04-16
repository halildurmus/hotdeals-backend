package com.halildurmus.hotdeals.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@TypeAlias("categories")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Category {

  @Id
  private String id;

  @NotBlank
  private String name;

  @NotBlank
  private String parent;

  @NotBlank
  private String category;

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public Category() {}

  public Category(String name, String parent, String category) {
    this.name = name;
    this.parent = parent;
    this.category = category;
  }

}
