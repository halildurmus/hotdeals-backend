package com.halildurmus.hotdeals.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@TypeAlias("category")
@Data
@NoArgsConstructor
public class Category implements Serializable {

  private static final long serialVersionUID = 1234567L;

  @Id
  private String id;

  @NotNull
  private Map<String, String> names = new HashMap<>();

  @NotBlank
  private String parent;

  @Indexed(unique = true)
  @NotBlank
  private String category;

  @NotBlank
  private String iconLigature;

  @NotBlank
  private String iconFontFamily;

  @CreatedDate
  // See https://github.com/spring-projects/spring-data-rest/issues/1565
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant createdAt;

  @LastModifiedDate
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant updatedAt;

  public Category(Map<String, String> names, String parent, String category, String iconLigature,
      String iconFontFamily) {
    this.names = names;
    this.parent = parent;
    this.category = category;
    this.iconLigature = iconLigature;
    this.iconFontFamily = iconFontFamily;
  }

}
