package com.halildurmus.hotdeals.deal.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halildurmus.hotdeals.util.ObjectIdJsonSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.Min;
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

  @Schema(description = "Store ID", type = "String", example = "5fbe790ec6f0b32014074bb3")
  @NotNull
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  private final ObjectId store;

  @Schema(description = "Category path", example = "/computers/monitors")
  @NotBlank
  private final String category;

  @Schema(description = "Deal title", example = "HP 24mh FHD Monitor with 23.8-Inch IPS Display (1080p)")
  @NotBlank
  @Size(min = 10, max = 100)
  private final String title;

  @Schema(description = "Deal description", example = "OUTSTANDING VISUALS – This FHD display with IPS technology gives you brilliant visuals and unforgettable quality; with a maximum resolution of 1920 x 1080 at 75 Hz, you’ll experience the image accuracy and wide-viewing spectrums of premium tablets and mobile devices ")
  @NotBlank
  @Size(min = 10, max = 3000)
  private final String description;

  @Schema(description = "Deal original price", example = "249.99")
  @NotNull
  @Min(1)
  private final Double originalPrice;

  @Schema(description = "Deal price", example = "226.99")
  @NotNull
  @Min(0)
  private final Double price;

  @Schema(description = "Deal cover photo URL", example = "https://www.gravatar.com/avatar")
  @URL
  @NotNull
  private final String coverPhoto;

  @Schema(description = "Deal URL", example = "https://www.amazon.com/HP-24mh-FHD-Monitor-Built/dp/B08BF4CZSV/")
  @URL
  @NotNull
  private final String dealUrl;

  @Schema(description = "Deal photo URLs", example = "https://www.gravatar.com/avatar")
  private final List<String> photos;

}
