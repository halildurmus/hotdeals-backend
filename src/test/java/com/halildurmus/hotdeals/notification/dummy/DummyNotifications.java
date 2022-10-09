package com.halildurmus.hotdeals.notification.dummy;

import com.halildurmus.hotdeals.notification.Notification;
import java.util.List;
import java.util.Map;

public class DummyNotifications {

  public static Notification notification1 =
      Notification.builder()
          .title("Title")
          .body("Body")
          .image("http://www.gravatar.com/avatar")
          .data(Map.of("uid", "jsdf235u2342fsah"))
          .tokens(List.of("423hjcs235"))
          .build();
}
