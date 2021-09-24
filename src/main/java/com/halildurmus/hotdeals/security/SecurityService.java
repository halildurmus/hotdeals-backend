package com.halildurmus.hotdeals.security;

import com.halildurmus.hotdeals.security.models.SecurityProperties;
import com.halildurmus.hotdeals.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  @Autowired
  SecurityProperties securityProperties;

  public User getUser() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Object principal = securityContext.getAuthentication().getPrincipal();

    if (principal instanceof User) {
      return ((User) principal);
    }

    return null;
  }

}