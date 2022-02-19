package com.halildurmus.hotdeals.category;

import com.halildurmus.hotdeals.audit.DateAudit;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@TypeAlias("category")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends DateAudit {

  private static final long serialVersionUID = 1234567L;

  @Id
  private String id;

  @NotNull
  private Map<String, String> names;

  @NotBlank
  private String parent;

  @Indexed(unique = true)
  @NotBlank
  private String category;

  @NotBlank
  private String iconLigature;

  @NotBlank
  private String iconFontFamily;

}
