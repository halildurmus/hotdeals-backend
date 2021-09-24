package com.halildurmus.hotdeals.security.models;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("security")
@Data
public class SecurityProperties {

  FirebaseProperties firebaseProperties;
  List<String> allowedOrigins;
  List<String> allowedMethods;
  List<String> superAdmins;

}