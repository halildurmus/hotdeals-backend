package com.halildurmus.hotdeals.notification;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface NotificationService {

  int sendNotification(Note note) throws FirebaseMessagingException;

}