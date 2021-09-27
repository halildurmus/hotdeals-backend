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

  @Indexed
  @NotBlank
  private String category;

  @Indexed
  @NotBlank
  private String title;

  @Indexed
  @NotBlank
  private String description;

  @NotNull
  private Double price;

  @NotNull
  private Double discountPrice;

  @URL
  @NotNull
  private String coverPhoto;

  @URL
  @NotNull
  private String dealUrl;

  private List<String> photos = new ArrayList<>();

  private int views = 0;

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public Deal(String title, String description, double price, double discountPrice, ObjectId store,
      String category, String coverPhoto, String dealUrl) {
    this.title = title;
    this.description = description;
    this.price = price;
    this.discountPrice = discountPrice;
    this.store = store;
    this.category = category;
    this.coverPhoto = coverPhoto;
    this.dealUrl = dealUrl;
  }

  public Deal(String title, String description, double price, double discountPrice, ObjectId store,
      String category, String coverPhoto, String dealUrl, List<String> photos) {
    this.title = title;
    this.description = description;
    this.price = price;
    this.discountPrice = discountPrice;
    this.store = store;
    this.category = category;
    this.coverPhoto = coverPhoto;
    this.dealUrl = dealUrl;
    this.photos = photos;
  }
}
