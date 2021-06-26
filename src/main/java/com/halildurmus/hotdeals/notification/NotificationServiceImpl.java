package com.halildurmus.hotdeals.notification;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  private FirebaseMessaging firebaseMessaging;

  public int sendNotification(Note note, List<String> tokens) throws FirebaseMessagingException {
    Notification notification = Notification
        .builder()
        .setTitle(note.getTitle())
        .setBody(note.getBody())
        .setImage(note.getImage())
        .build();

    MulticastMessage message = MulticastMessage.builder()
        .setNotification(notification)
        .putAllData(note.getData())
        .addAllTokens(tokens)
        .build();

    BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
    System.out.println(response.getSuccessCount() + " messages were sent successfully");

    return response.getSuccessCount();
  }

}