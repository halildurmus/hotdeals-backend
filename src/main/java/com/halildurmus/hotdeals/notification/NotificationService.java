package com.halildurmus.hotdeals.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import java.util.List;

public interface NotificationService {

  int sendNotification(Note note, List<String> tokens) throws FirebaseMessagingException;

}