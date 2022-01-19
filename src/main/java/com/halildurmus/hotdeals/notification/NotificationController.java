package com.halildurmus.hotdeals.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "notifications")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/notifications")
@Validated
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Sends a push notification using FCM", description = "<b>*</b>(<b>title</b> or <b>titleLocKey</b>) and (<b>body</b> or <b>bodyLocKey</b>) parameters are required")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Push notification sent", content = @Content(schema = @Schema(type = "integer", defaultValue = "1"))),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
  })
  public Integer sendNotification(@Valid @RequestBody Notification notification) {
    if (ObjectUtils.isEmpty(notification.getTitle()) && ObjectUtils.isEmpty(
        notification.getTitleLocKey())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "title or titleLocKey parameters cannot be empty");
    } else if (ObjectUtils.isEmpty(notification.getBody()) && ObjectUtils.isEmpty(
        notification.getBodyLocKey())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "body or bodyLocKey parameters cannot be empty");
    }

    return notificationService.send(notification);
  }

}
