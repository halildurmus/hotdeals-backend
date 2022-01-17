package com.halildurmus.hotdeals.store.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@ApiModel("StorePostDTO")
@Data
@Builder
public class StorePostDTO {

  @ApiModelProperty(value = "Store name", position = 1, example = "Amazon", required = true)
  @NotBlank
  private final String name;

  @ApiModelProperty(value = "Store logo URL", position = 2, example = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Amazon_logo.svg/2560px-Amazon_logo.svg.png", required = true)
  @URL
  @NotBlank
  private final String logo;

}