package com.halildurmus.hotdeals.notification;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("Notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

  @ApiModelProperty(value = "Notification title", position = 1, example = "A new message")
  private String title;

  @ApiModelProperty(value = "Notification titleLocKey", position = 2, example = "text_message_title")
  private String titleLocKey;

  @ApiModelProperty(value = "Notification body", position = 3, example = "How are you?")
  private String body;

  @ApiModelProperty(value = "Notification bodyLocKey", position = 4, example = "image_message_body")
  private String bodyLocKey;

  @ApiModelProperty(value = "Notification image URL", position = 5, example = "https://www.gravatar.com/avatar")
  private String image;

  @ApiModelProperty(value = "Notification titleLocArgs", position = 6, example = "[\"MrNobody123\"]")
  private List<String> titleLocArgs = new ArrayList<>();

  @ApiModelProperty(value = "Notification bodyLocArgs", position = 7, example = "[\"MrNobody123\"]")
  private List<String> bodyLocArgs = new ArrayList<>();

  @ApiModelProperty(value = "Notification data", position = 8, example = "{\"verb\": \"message\", \"object\": \"Ybeuoz0E2oObgUA1Uif8dbXjHX62_ndj2KkbGwIUbfIUH2BT6700AQ832\", \"avatar\": \"https://www.gravatar.com/avatar\", \"message\": \"How are you?\", \"uid\": \"ndj2KkbGwIUbfIUH2BT6700AQ832\"}", required = true)
  @NotNull
  private Map<String, String> data;

  @ApiModelProperty(value = "Notification FCM tokens", position = 9, example = "[dOMvrfckR9-5R_A43nuFMo:APA91bEVh2JQ8i-l1406C68mExotHQCGWeRc0cuLZTDH9t5vXXWIPZ-6HDaOtn1PLipsqWbpNWVcpDxkcIWwHNR60_mtaRo5kyuf0cs5Fxa6iGLpoqV93rpWIisa9_acGbOZwfIass0B]", required = true)
  @NotEmpty
  private List<String> tokens;

}