package com.halildurmus.hotdeals.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @PostMapping("/notifications")
  public ResponseEntity<Object> sendNotification(@RequestBody Note note,
      @RequestParam List<String> tokens) {
    try {
      final int response = notificationService.sendNotification(note, tokens);

      return ResponseEntity.status(201).body(response);
    } catch (FirebaseMessagingException e) {
      return ResponseEntity.status(400).body(HttpStatus.BAD_REQUEST);
    }
  }

}
