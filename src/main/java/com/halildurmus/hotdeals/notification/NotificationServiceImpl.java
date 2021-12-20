package com.halildurmus.hotdeals.notification;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.user.UserService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  private FirebaseMessaging firebaseMessaging;

  @Autowired
  private SecurityService securityService;

  @Autowired
  private UserService userService;

  public int sendNotification(Note note) {
    final User user = securityService.getUser();
    final Map<String, String> data = note.getData();
    data.put("actor", user.getId());

    final Notification notification = Notification
        .builder()
        .setBody(note.getBodyLocArgs().get(0))
        .build();

    // TODO(halildurmus): add back image property
    final AndroidNotification androidNotification = AndroidNotification
        .builder()
        .setTitleLocalizationKey(note.getTitleLocKey())
        .addAllTitleLocalizationArgs(note.getTitleLocArgs())
        .setBodyLocalizationKey(note.getBodyLocKey())
        .addAllBodyLocalizationArgs(note.getBodyLocArgs())
        .setPriority(AndroidNotification.Priority.MAX)
        .build();

    final AndroidConfig androidConfig = AndroidConfig.builder()
        .setNotification(androidNotification)
        .setPriority(Priority.HIGH).build();

    final MulticastMessage message = MulticastMessage.builder()
        .setNotification(notification)
        .setAndroidConfig(androidConfig)
        .putAllData(note.getData())
        .addAllTokens(note.getTokens())
        .build();

    BatchResponse batchResponse;
    try {
      batchResponse = firebaseMessaging.sendMulticast(message);
    } catch (FirebaseMessagingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "", e);
    }
    log.debug(batchResponse.getSuccessCount() + " messages were sent successfully");

    // Removes invalid FCM tokens
    for (int i = 0; i < batchResponse.getResponses().size(); i++) {
      final SendResponse sendResponse = batchResponse.getResponses().get(i);
      if (sendResponse.getException() != null) {
        final String errorCode = sendResponse.getException().getMessagingErrorCode().name();
        if (errorCode.equals("INVALID_ARGUMENT") || errorCode.equals("UNREGISTERED")) {
          final String fcmToken = note.getTokens().get(i);
          final String userUid = note.getData().get("uid");
          userService.removeFcmToken(userUid, fcmToken);
        }
      }
    }

    return batchResponse.getSuccessCount();
  }

}