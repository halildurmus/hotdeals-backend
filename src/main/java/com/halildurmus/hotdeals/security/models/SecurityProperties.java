package com.halildurmus.hotdeals.security.models;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("security")
@Data
public class SecurityProperties {

  FirebaseProperties firebaseProps;
  boolean allowCredentials;
  List<String> allowedOrigins;
  List<String> allowedHeaders;
  List<String> exposedHeaders;
  List<String> allowedMethods;
  List<String> allowedPublicApis;
  List<String> superAdmins;
  List<String> validApplicationRoles;

}