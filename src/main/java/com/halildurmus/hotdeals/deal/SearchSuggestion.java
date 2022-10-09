package com.halildurmus.hotdeals.deal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchSuggestion {

  @Schema(description = "Deal ID", example = "5fbe790ec6f0b32014074bb2")
  private final String id;

  @Schema(description = "Deal title", example = "iPhone 12 Pro Max 128 GB")
  private final String title;
}
