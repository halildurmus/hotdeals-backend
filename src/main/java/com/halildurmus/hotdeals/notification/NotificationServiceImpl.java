package com.halildurmus.hotdeals.notification;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.halildurmus.hotdeals.user.UserService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  private FirebaseMessaging firebaseMessaging;

  @Autowired
  private UserService userService;

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

    BatchResponse batchResponse = firebaseMessaging.sendMulticast(message);
    log.debug(batchResponse.getSuccessCount() + " messages were sent successfully");

    for (int i = 0; i < batchResponse.getResponses().size(); i++) {
      final SendResponse sendResponse = batchResponse.getResponses().get(i);
      if (sendResponse.getException() != null) {
        final String errorCode = sendResponse.getException().getMessagingErrorCode().name();
        if (errorCode.equals("INVALID_ARGUMENT") || errorCode.equals("UNREGISTERED")) {
          final String fcmToken = tokens.get(i);
          final String userUid = note.getData().get("uid");
          userService.removeFcmToken(userUid, fcmToken);
        }
      }
    }

    return batchResponse.getSuccessCount();
  }

}