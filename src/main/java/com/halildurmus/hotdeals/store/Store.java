package com.halildurmus.hotdeals.store;

import com.halildurmus.hotdeals.audit.DateAudit;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stores")
@TypeAlias("store")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Store extends DateAudit {

  private static final long serialVersionUID = 1234567L;

  @Id private String id;

  @NotBlank private String name;

  @URL @NotNull private String logo;
}
