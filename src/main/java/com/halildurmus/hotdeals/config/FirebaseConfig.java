package com.halildurmus.hotdeals.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FirebaseConfig {

  private FirebaseOptions getFirebaseOptions() throws IOException {
    final String env = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
    // If 'GOOGLE_APPLICATION_CREDENTIALS' is present in the environment variables,
    // use it to get the credentials.
    if (env != null) {
      return FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.getApplicationDefault()).build();
    }
    // Otherwise, read the credentials directly from the json file.
    final String filePath = "src/main/resources/firebase-admin.json";
    final FileInputStream serviceAccount = new FileInputStream(filePath);

    return FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
  }

  @Primary
  @Bean
  public FirebaseApp getFirebaseApp() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {
      FirebaseApp.initializeApp(getFirebaseOptions());
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