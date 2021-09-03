package com.halildurmus.hotdeals.notification;

import java.util.List;
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
  public int sendNotification(@RequestBody Note note, @RequestParam List<String> tokens)
      throws Exception {
    return notificationService.sendNotification(note, tokens);
  }

}
