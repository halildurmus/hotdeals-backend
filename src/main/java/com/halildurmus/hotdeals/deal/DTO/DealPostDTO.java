package com.halildurmus.hotdeals.deal.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class DealPostDTO {

  @NotNull
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private final ObjectId store;

  @NotBlank
  private final String category;

  @NotBlank
  @Size(min = 10, max = 100)
  private final String title;

  @NotBlank
  @Size(min = 10, max = 3000)
  private final String description;

  @NotNull
  private final Double originalPrice;

  @NotNull
  private final Double price;

  @URL
  @NotNull
  private final String coverPhoto;

  @URL
  @NotNull
  private final String dealUrl;

  private final List<String> photos;

}
