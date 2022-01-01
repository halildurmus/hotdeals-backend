package com.halildurmus.hotdeals.deal.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.deal.DealStatus;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import com.halildurmus.hotdeals.util.ObjectIdSetJsonSerializer;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class DealGetDTO {

  private final String id;

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private final ObjectId postedBy;

  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private final ObjectId store;

  private final int dealScore;

  @JsonSerialize(using = ObjectIdSetJsonSerializer.class)
  private final HashSet<ObjectId> upvoters;

  @JsonSerialize(using = ObjectIdSetJsonSerializer.class)
  private final HashSet<ObjectId> downvoters;

  private final String category;

  private final String title;

  private final String description;

  private final Double originalPrice;

  private final Double price;

  private final String coverPhoto;

  private final String dealUrl;

  private final List<String> photos;

  private final int views;

  private final DealStatus status;

  private final Instant createdAt;

}
