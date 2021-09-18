package com.halildurmus.hotdeals.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
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

  @NotBlank
  private String iconLigature;

  @NotBlank
  private String iconFontFamily;

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public Category(String name, String parent, String category, String iconLigature,
      String iconFontFamily) {
    this.name = name;
    this.parent = parent;
    this.category = category;
    this.iconLigature = iconLigature;
    this.iconFontFamily = iconFontFamily;
  }

}
