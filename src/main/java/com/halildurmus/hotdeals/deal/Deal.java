package com.halildurmus.hotdeals.deal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import com.halildurmus.hotdeals.util.ObjectIdSetJsonSerializer;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "deals")
@TypeAlias("deal")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deal implements Serializable {

  private static final long serialVersionUID = 1234567L;

  @Id
  private String id;

  @Indexed
  @JsonProperty(access = Access.READ_ONLY)
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId postedBy;

  @NotNull
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId store;

  private int dealScore = 0;

  @JsonSerialize(using = ObjectIdSetJsonSerializer.class)
  private HashSet<ObjectId> upvoters = new HashSet<>();

  @JsonSerialize(using = ObjectIdSetJsonSerializer.class)
  private HashSet<ObjectId> downvoters = new HashSet<>();

  @NotBlank
  private String category;

  @NotBlank
  @Size(min = 10, max = 100)
  private String title;

  @NotBlank
  @Size(min = 10, max = 3000)
  private String description;

  @NotNull
  private Double originalPrice;

  @NotNull
  private Double price;

  @URL
  @NotNull
  private String coverPhoto;

  @URL
  @NotNull
  private String dealUrl;

  private DealStatus status = DealStatus.ACTIVE;

  private List<String> photos = new ArrayList<>();

  private int views = 0;

  @CreatedDate
  // See https://github.com/spring-projects/spring-data-rest/issues/1565
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant createdAt;

  @LastModifiedDate
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant updatedAt;

}
