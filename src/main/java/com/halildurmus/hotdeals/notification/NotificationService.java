package com.halildurmus.hotdeals.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  @Autowired
  private FirebaseMessaging firebaseMessaging;

  public String sendNotification(Note note, String token) throws FirebaseMessagingException {
    Notification notification = Notification
        .builder()
        .setTitle(note.getTitle())
        .setBody(note.getBody())
        .setImage(note.getImage())
        .build();

    Message message = Message
        .builder()
        .setToken(token)
        .setNotification(notification)
        .putAllData(note.getData())
        .build();

    return firebaseMessaging.send(message);
  }

}