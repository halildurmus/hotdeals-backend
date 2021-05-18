package com.halildurmus.hotdeals.deal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.util.ObjectIdArrayJsonSerializer;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class Deal {

  @Id
  private String id;

  @Indexed
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId postedBy;

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  @NotNull
  private ObjectId store;

  private int dealScore = 0;

  @JsonSerialize(using = ObjectIdArrayJsonSerializer.class)
  private List<ObjectId> upVoters = new ArrayList<>();

  @JsonSerialize(using = ObjectIdArrayJsonSerializer.class)
  private List<ObjectId> downVoters = new ArrayList<>();

  @Indexed
  @NotBlank
  private String category;

  @NotBlank
  private String title;

  @NotBlank
  private String description;

  @URL
  @NotNull
  private String coverPhoto;

  // @URL
  private List<String> photos = new ArrayList<>();

  private int views = 0;

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public Deal(String title, String description, ObjectId store, String category, String coverPhoto) {
    this.title = title;
    this.description = description;
    this.store = store;
    this.category = category;
    this.coverPhoto = coverPhoto;
  }

  public Deal(String title, String description, ObjectId store, String category, String coverPhoto,
      List<String> photos) {
    this.title = title;
    this.description = description;
    this.store = store;
    this.category = category;
    this.coverPhoto = coverPhoto;
    this.photos = photos;
  }
}
