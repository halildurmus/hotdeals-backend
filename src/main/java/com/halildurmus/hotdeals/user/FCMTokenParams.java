package com.halildurmus.hotdeals.user;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "FCMTokenParams")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FCMTokenParams {

  @Schema(description = "Unique device ID", example = "29ba634d38ac6a1a", required = true)
  private String deviceId;

  @Schema(description = "FCM token", example = "dOMvrfckR9-5R_A43nuFMo:APA91bEVh2JQ8i-l1406C68mExotHQCGWeRc0cuLZTDH9t5vXXWIPZ-6HDaOtn1PLipsqWbpNWVcpDxkcIWwHNR60_mtaRo5kyuf0cs5Fxa6iGLpoqV93rpWIisa9_acGbOZwfIass0B")
  @NotBlank
  private String token;

}
