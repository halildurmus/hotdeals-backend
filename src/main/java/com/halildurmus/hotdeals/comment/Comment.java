package com.halildurmus.hotdeals.comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
@TypeAlias("comments")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class Comment {

  @Id
  private String id;

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId dealId;

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId postedBy;

  @NotBlank
  private String message;

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public Comment(ObjectId dealId, ObjectId postedBy, String message) {
    this.dealId = dealId;
    this.postedBy = postedBy;
    this.message = message;
  }

}