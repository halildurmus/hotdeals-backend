package com.halildurmus.hotdeals.user;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FCMTokenParams {

  private String deviceId;

  @NotBlank
  private String token;

}
