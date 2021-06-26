package com.halildurmus.hotdeals.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @PostMapping("/notifications")
  public String sendNotification(@RequestBody Note note, @RequestParam String token)
      throws FirebaseMessagingException {
    return notificationService.sendNotification(note, token);
  }

}
