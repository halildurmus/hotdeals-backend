package com.halildurmus.hotdeals.security;

import com.halildurmus.hotdeals.security.models.Credentials;
import com.halildurmus.hotdeals.security.models.SecurityProperties;
import com.halildurmus.hotdeals.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  @Autowired
  SecurityProperties securityProps;

  public User getUser() {
    User userPrincipal = null;
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Object principal = securityContext.getAuthentication().getPrincipal();

    if (principal instanceof User) {
      userPrincipal = ((User) principal);
    }

    return userPrincipal;
  }

  public Credentials getCredentials() {
    SecurityContext securityContext = SecurityContextHolder.getContext();

    return (Credentials) securityContext.getAuthentication().getCredentials();
  }

}