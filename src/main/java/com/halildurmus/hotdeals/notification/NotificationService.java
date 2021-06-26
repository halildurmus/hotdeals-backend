package com.halildurmus.hotdeals.notification;

import java.util.List;

public interface NotificationService {

  int sendNotification(Note note, List<String> tokens) throws Exception;

}