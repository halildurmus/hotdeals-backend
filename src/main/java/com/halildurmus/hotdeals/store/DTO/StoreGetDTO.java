package com.halildurmus.hotdeals.store.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@ApiModel("StoreGetDTO")
@Data
@Builder
public class StoreGetDTO {

  @ApiModelProperty(value = "Store id", position = 1, example = "5fbe790ec6f0b32014074bb1")
  private final String id;

  @ApiModelProperty(value = "Store name", position = 2, example = "Amazon")
  private final String name;

  @ApiModelProperty(value = "Store logo URL", position = 3, example = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Amazon_logo.svg/2560px-Amazon_logo.svg.png")
  private final String logo;

}
