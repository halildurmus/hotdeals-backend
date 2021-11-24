package com.halildurmus.hotdeals.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
@TypeAlias("comment")
@Data
@NoArgsConstructor
public class Comment implements Serializable {

  private static final long serialVersionUID = 1234567L;

  @Id
  private String id;

  @Indexed
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId dealId;

  @JsonProperty(access = Access.READ_ONLY)
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId postedBy;

  @NotBlank
  @Size(min = 1, max = 500)
  private String message;

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public Comment(ObjectId dealId, String message) {
    this.dealId = dealId;
    this.message = message;
  }

}
