package com.halildurmus.hotdeals.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@TypeAlias("categories")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class Category implements Serializable {

  private static final long serialVersionUID = 1234567L;

  @Id
  private String id;

  @NotBlank
  private String name;

  @NotBlank
  private String parent;

  @NotBlank
  private String category;

  @NotNull
  private Map<String, Object> icon;

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public Category(String name, String parent, String category, Map<String, Object> icon) {
    this.name = name;
    this.parent = parent;
    this.category = category;
    this.icon = icon;
  }

}
