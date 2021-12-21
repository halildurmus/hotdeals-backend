package com.halildurmus.hotdeals.notification;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @PostMapping("/notifications")
  public ResponseEntity<Object> sendNotification(@Valid @RequestBody Note note) {
    if (ObjectUtils.isEmpty(note.getTitle()) && ObjectUtils.isEmpty(note.getTitleLocKey())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "title or titleLocKey parameters cannot be empty");
    } else if (ObjectUtils.isEmpty(note.getBody()) && ObjectUtils.isEmpty(note.getBodyLocKey())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "body or bodyLocKey parameters cannot be empty");
    }

    final int successCount = notificationService.sendNotification(note);

    return ResponseEntity.status(201).body(successCount);
  }

}
