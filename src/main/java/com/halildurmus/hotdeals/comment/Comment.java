package com.halildurmus.hotdeals.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.audit.DateAudit;
import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "comments")
@TypeAlias("comment")
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends DateAudit {

  private static final long serialVersionUID = 1234567L;

  @Id private String id;

  @NotNull
  @Indexed
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId dealId;

  @DocumentReference
  @JsonProperty(access = Access.READ_ONLY)
  private User postedBy;

  @NotBlank
  @Size(min = 1, max = 500)
  private String message;
}
