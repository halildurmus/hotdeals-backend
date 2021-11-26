package com.halildurmus.hotdeals.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @PostMapping("/notifications")
  public ResponseEntity<Object> sendNotification(@Valid @RequestBody Note note) {
    try {
      final int response = notificationService.sendNotification(note);

      return ResponseEntity.status(201).body(response);
    } catch (FirebaseMessagingException e) {
      return ResponseEntity.status(400).body(HttpStatus.BAD_REQUEST);
    }
  }

}
