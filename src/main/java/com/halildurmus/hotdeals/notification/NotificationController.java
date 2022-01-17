package com.halildurmus.hotdeals.notification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
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

@Api(tags = "notifications")
@RestController
@RequestMapping("/notifications")
@Validated
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Sends a push notification using FCM(Firebase Cloud Messaging)", notes = "<b>*</b>(<b>title</b> or <b>titleLocKey</b>) and (<b>body</b> or <b>bodyLocKey</b>) parameters are required", authorizations = @Authorization("Bearer"))
  @ApiResponses({
      @ApiResponse(code = 201, message = "Push notification sent", examples = @Example(@ExampleProperty("1"))),
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 401, message = "Unauthorized")
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
