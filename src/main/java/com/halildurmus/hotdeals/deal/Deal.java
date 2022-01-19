package com.halildurmus.hotdeals.deal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import com.halildurmus.hotdeals.util.ObjectIdSetJsonSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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

  @Schema(description = "Deal ID", example = "5fbe790ec6f0b32014074bb2")
  @Id
  private String id;

  @Indexed
  @JsonProperty(access = Access.READ_ONLY)
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId postedBy;

  @NotNull
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private ObjectId store;

  @Schema(description = "Deal score", example = "3")
  private int dealScore = 0;

  @Default
  @JsonSerialize(using = ObjectIdSetJsonSerializer.class)
  private HashSet<ObjectId> upvoters = new HashSet<>();

  @Default
  @JsonSerialize(using = ObjectIdSetJsonSerializer.class)
  private HashSet<ObjectId> downvoters = new HashSet<>();

  @Schema(description = "Category path", example = "/computers/monitors")
  @NotBlank
  private String category;

  @Schema(description = "Deal title", example = "HP 24mh FHD Monitor with 23.8-Inch IPS Display (1080p)")
  @NotBlank
  @Size(min = 10, max = 100)
  private String title;

  @Schema(description = "Deal description", example = "OUTSTANDING VISUALS – This FHD display with IPS technology gives you brilliant visuals and unforgettable quality; with a maximum resolution of 1920 x 1080 at 75 Hz, you’ll experience the image accuracy and wide-viewing spectrums of premium tablets and mobile devices ")
  @NotBlank
  @Size(min = 10, max = 3000)
  private String description;

  @Schema(description = "Deal original price", example = "249.99")
  @NotNull
  @Min(1)
  private Double originalPrice;

  @Schema(description = "Deal price", example = "226.99")
  @NotNull
  @Min(0)
  private Double price;

  @Schema(description = "Deal cover photo URL", example = "https://www.gravatar.com/avatar")
  @URL
  @NotNull
  private String coverPhoto;

  @Schema(description = "Deal URL", example = "https://www.amazon.com/HP-24mh-FHD-Monitor-Built/dp/B08BF4CZSV/")
  @URL
  @NotNull
  private String dealUrl;

  @Default
  private DealStatus status = DealStatus.ACTIVE;

  @Schema(description = "Deal photo URLs", example = "[https://www.gravatar.com/avatar]")
  private List<String> photos = new ArrayList<>();

  @Schema(description = "Deal views", example = "10")
  private int views = 0;

  @CreatedDate
  // See https://github.com/spring-projects/spring-data-rest/issues/1565
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant createdAt;

  @LastModifiedDate
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant updatedAt;

}
