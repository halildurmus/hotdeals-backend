package com.halildurmus.hotdeals.deal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.util.ObjectIdArrayJsonSerializer;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@Data
@NoArgsConstructor
public class Deal implements Serializable {

  private static final long serialVersionUID = 1234567L;

  @Id
  private String id;

  @Indexed
  @JsonProperty(access = Access.READ_ONLY)
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId postedBy;

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  @NotNull
  private ObjectId store;

  private int dealScore = 0;

  @JsonSerialize(using = ObjectIdArrayJsonSerializer.class)
  private List<ObjectId> upvoters = new ArrayList<>();

  @JsonSerialize(using = ObjectIdArrayJsonSerializer.class)
  private List<ObjectId> downvoters = new ArrayList<>();

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

  private Boolean isExpired = false;

  private List<String> photos = new ArrayList<>();

  private int views = 0;

  @CreatedDate
  // See https://github.com/spring-projects/spring-data-rest/issues/1565
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public Deal(String title, String description, double originalPrice, double price, ObjectId store,
      String category, String coverPhoto, String dealUrl) {
    this.title = title;
    this.description = description;
    this.originalPrice = originalPrice;
    this.price = price;
    this.store = store;
    this.category = category;
    this.coverPhoto = coverPhoto;
    this.dealUrl = dealUrl;
  }

  public Deal(String title, String description, double originalPrice, double price, ObjectId store,
      String category, String coverPhoto, String dealUrl, List<String> photos) {
    this.title = title;
    this.description = description;
    this.originalPrice = originalPrice;
    this.price = price;
    this.store = store;
    this.category = category;
    this.coverPhoto = coverPhoto;
    this.dealUrl = dealUrl;
    this.photos = photos;
  }

}
