package com.halildurmus.hotdeals.store.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreGetDTO {

  private final String id;

  private final String name;

  private final String logo;

}
