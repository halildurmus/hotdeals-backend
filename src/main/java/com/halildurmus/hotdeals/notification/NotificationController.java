package com.halildurmus.hotdeals.notification;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    if (note.getTitleLocArgs().isEmpty() || note.getBodyLocArgs().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "titleLocArgs and bodyLocArgs parameters cannot be empty");
    }
    final int successCount = notificationService.sendNotification(note);

    return ResponseEntity.status(201).body(successCount);
  }

}
