package com.halildurmus.hotdeals.deal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
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
public class Deal {

  @Id
  private String id;

  @Indexed
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId postedBy;

  @NotBlank
  private String title;

  @NotBlank
  private String description;

  @URL
  private String coverPhoto;

  // @URL
  private List<String> photos = new ArrayList<>();

  private int likes = 0;

  private int views = 0;

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public Deal() {}

  public Deal(String title, String description, String coverPhoto) {
    this.title = title;
    this.description = description;
    this.coverPhoto = coverPhoto;
  }

  public Deal(String title, String description, String coverPhoto, List<String> photos) {
    this.title = title;
    this.description = description;
    this.coverPhoto = coverPhoto;
    this.photos = photos;
  }
}
