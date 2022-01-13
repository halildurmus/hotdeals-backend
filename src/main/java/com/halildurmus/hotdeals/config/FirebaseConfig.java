package com.halildurmus.hotdeals.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.halildurmus.hotdeals.security.models.SecurityProperties;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FirebaseConfig {

  @Autowired
  private SecurityProperties securityProperties;

  @Primary
  @Bean
  public FirebaseApp getFirebaseApp() throws IOException {
    final FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .setDatabaseUrl(securityProperties.getFirebaseProperties().getDatabaseUrl()).build();

    if (FirebaseApp.getApps().isEmpty()) {
      FirebaseApp.initializeApp(options);
    }

    return FirebaseApp.getInstance();
  }

  @Bean
  public FirebaseAuth getAuth() throws IOException {
    return FirebaseAuth.getInstance(getFirebaseApp());
  }

  @Bean
  public FirebaseMessaging getMessaging() throws IOException {
    return FirebaseMessaging.getInstance(getFirebaseApp());
  }

}