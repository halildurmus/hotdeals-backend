package com.halildurmus.hotdeals.notification;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import com.halildurmus.hotdeals.security.SecurityService;
import com.halildurmus.hotdeals.user.FCMTokenParams;
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

  private MulticastMessage createMulticastMessage(Notification notification) {
    final User user = securityService.getUser();
    final Map<String, String> data = notification.getData();
    data.put("actor", user.getId());

    final com.google.firebase.messaging.Notification firebaseNotification = com.google.firebase.messaging.Notification
        .builder()
        .setTitle(notification.getTitle())
        .setBody(notification.getBody())
        .build();

    final AndroidNotification androidNotification = AndroidNotification
        .builder()
        .setImage(notification.getImage())
        .setTitle(notification.getTitle())
        .setBody(notification.getBody())
        .setTitleLocalizationKey(notification.getTitleLocKey())
        .addAllTitleLocalizationArgs(notification.getTitleLocArgs())
        .setBodyLocalizationKey(notification.getBodyLocKey())
        .addAllBodyLocalizationArgs(notification.getBodyLocArgs())
        .setPriority(AndroidNotification.Priority.MAX)
        .build();

    final AndroidConfig androidConfig = AndroidConfig.builder()
        .setNotification(androidNotification)
        .setPriority(Priority.HIGH).build();

    return MulticastMessage.builder()
        .setNotification(firebaseNotification)
        .setAndroidConfig(androidConfig)
        .putAllData(notification.getData())
        .addAllTokens(notification.getTokens())
        .build();
  }

  public int send(Notification notification) {
    final MulticastMessage message = createMulticastMessage(notification);
    BatchResponse batchResponse;
    try {
      batchResponse = firebaseMessaging.sendMulticast(message);
      log.debug(batchResponse.getSuccessCount() + " messages were sent successfully");
    } catch (FirebaseMessagingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "", e);
    }

    // Removes invalid FCM tokens
    for (int i = 0; i < batchResponse.getResponses().size(); i++) {
      final SendResponse sendResponse = batchResponse.getResponses().get(i);
      if (sendResponse.getException() != null) {
        final String errorCode = sendResponse.getException().getMessagingErrorCode().name();
        if (errorCode.equals("INVALID_ARGUMENT") || errorCode.equals("UNREGISTERED")) {
          final String userUid = notification.getData().get("uid");
          final String fcmToken = notification.getTokens().get(i);
          final FCMTokenParams fcmTokenParams = FCMTokenParams.builder().token(fcmToken).build();
          userService.deleteFCMToken(userUid, fcmTokenParams);
          log.debug(fcmToken + " were removed successfully from the user");
        }
      }
    }

    return batchResponse.getSuccessCount();
  }

}