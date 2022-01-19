package com.halildurmus.hotdeals.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

  @Schema(description = "Notification title", example = "A new message")
  private String title;

  @Schema(description = "Notification titleLocKey", example = "text_message_title")
  private String titleLocKey;

  @Schema(description = "Notification body", example = "How are you?")
  private String body;

  @Schema(description = "Notification bodyLocKey", example = "image_message_body")
  private String bodyLocKey;

  @Schema(description = "Notification image URL", example = "https://www.gravatar.com/avatar")
  private String image;

  @Schema(description = "Notification titleLocArgs", example = "[\"MrNobody123\"]")
  private List<String> titleLocArgs = new ArrayList<>();

  @Schema(description = "Notification bodyLocArgs", example = "[\"MrNobody123\"]")
  private List<String> bodyLocArgs = new ArrayList<>();

  @Schema(description = "Notification data", example = "{\"verb\": \"message\", \"object\": \"Ybeuoz0E2oObgUA1Uif8dbXjHX62_ndj2KkbGwIUbfIUH2BT6700AQ832\", \"avatar\": \"https://www.gravatar.com/avatar\", \"message\": \"How are you?\", \"uid\": \"ndj2KkbGwIUbfIUH2BT6700AQ832\"}", required = true)
  @NotNull
  private Map<String, String> data;

  @Schema(description = "Notification FCM tokens", example = "[dOMvrfckR9-5R_A43nuFMo:APA91bEVh2JQ8i-l1406C68mExotHQCGWeRc0cuLZTDH9t5vXXWIPZ-6HDaOtn1PLipsqWbpNWVcpDxkcIWwHNR60_mtaRo5kyuf0cs5Fxa6iGLpoqV93rpWIisa9_acGbOZwfIass0B]", required = true)
  @NotEmpty
  private List<String> tokens;

}