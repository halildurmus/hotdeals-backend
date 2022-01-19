package com.halildurmus.hotdeals.deal.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.deal.DealStatus;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import com.halildurmus.hotdeals.util.ObjectIdSetJsonSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class DealGetDTO {

  @Schema(description = "Deal ID", example = "5fbe790ec6f0b32014074bb2")
  private final String id;

  @Schema(description = "Posted user", type = "String", example = "5fbe790ec6f0b32014074bb1")
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private final ObjectId postedBy;

  @Schema(description = "Store ID", type = "String", example = "5fbe790ec6f0b32014074bb3")
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private final ObjectId store;

  @Schema(description = "Deal score", example = "3")
  private final int dealScore;

  @Schema(description = "Deal upvoters", example = "[5fbe790ec6f0b32014074bb1,5fbe790ec6f0b32014074bb2]")
  @JsonSerialize(using = ObjectIdSetJsonSerializer.class)
  private final HashSet<ObjectId> upvoters;

  @Schema(description = "Deal downvoters", example = "[5fbe790ec6f0b32014074bb3]")
  @JsonSerialize(using = ObjectIdSetJsonSerializer.class)
  private final HashSet<ObjectId> downvoters;

  @Schema(description = "Category path", example = "/computers/monitors")
  private final String category;

  @Schema(description = "Deal title", example = "HP 24mh FHD Monitor with 23.8-Inch IPS Display (1080p)")
  private final String title;

  @Schema(description = "Deal description", example = "OUTSTANDING VISUALS – This FHD display with IPS technology gives you brilliant visuals and unforgettable quality; with a maximum resolution of 1920 x 1080 at 75 Hz, you’ll experience the image accuracy and wide-viewing spectrums of premium tablets and mobile devices ")
  private final String description;

  @Schema(description = "Deal original price", example = "249.99")
  private final Double originalPrice;

  @Schema(description = "Deal price", example = "226.99")
  private final Double price;

  @Schema(description = "Deal cover photo URL", example = "https://www.gravatar.com/avatar")
  private final String coverPhoto;

  @Schema(description = "Deal URL", example = "https://www.amazon.com/HP-24mh-FHD-Monitor-Built/dp/B08BF4CZSV/")
  private final String dealUrl;

  @Schema(description = "Deal photo URLs", example = "[https://www.gravatar.com/avatar]")
  private final List<String> photos;

  @Schema(description = "Deal views", example = "10")
  private final int views;

  @Schema(description = "Deal status", example = "ACTIVE")
  private final DealStatus status;

  @Schema(description = "Deal createdAt", example = "2021-06-30T16:36:59.713Z")
  private final Instant createdAt;

}
