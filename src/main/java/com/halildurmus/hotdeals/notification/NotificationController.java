package com.halildurmus.hotdeals.notification;

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

@RestController
@RequestMapping("/notifications")
@Validated
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
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
