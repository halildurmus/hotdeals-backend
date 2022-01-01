package com.halildurmus.hotdeals.security;

import com.halildurmus.hotdeals.user.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  /**
   * Returns the authenticated {@code User}.
   *
   * @return {@code User}
   */
  public User getUser() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Object principal = securityContext.getAuthentication().getPrincipal();

    if (principal instanceof User) {
      return ((User) principal);
    }

    return null;
  }

}