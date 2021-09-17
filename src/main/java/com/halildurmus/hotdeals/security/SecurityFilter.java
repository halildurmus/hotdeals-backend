package com.halildurmus.hotdeals.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.halildurmus.hotdeals.security.models.Credentials;
import com.halildurmus.hotdeals.security.models.SecurityProperties;
import com.halildurmus.hotdeals.security.role.RoleConstants;
import com.halildurmus.hotdeals.security.role.RoleService;
import com.halildurmus.hotdeals.user.User;
import com.halildurmus.hotdeals.user.UserRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

  @Autowired
  SecurityService securityService;

  @Autowired
  SecurityProperties securityProps;

  @Autowired
  RoleService roleService;

  @Autowired
  UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    verifyToken(request);
    filterChain.doFilter(request, response);
  }

  public String parseBearerToken(HttpServletRequest request) {
    String bearerToken = null;
    String authHeader = request.getHeader("Authorization");

    if (ObjectUtils.isNotEmpty(authHeader) && authHeader.startsWith("Bearer ")
        && authHeader.length() > 50) {
      bearerToken = authHeader.substring(7);
    }

    return bearerToken;
  }

  private void verifyToken(HttpServletRequest request) {
    FirebaseToken decodedToken = null;
    String token = parseBearerToken(request);
    logger.info("Token: " + token);

    try {
      if (ObjectUtils.isNotEmpty(token)) {
        decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
      }
    } catch (FirebaseAuthException e) {
      e.printStackTrace();
      log.error("Firebase Exception:: {}", e.getLocalizedMessage());
    }

    User user = firebaseTokenToUserDto(decodedToken);
    List<GrantedAuthority> authorities = new ArrayList<>();

    if (user != null && decodedToken != null) {
      // Handle Super role
      if (securityProps.getSuperAdmins() != null && securityProps.getSuperAdmins()
          .contains(user.getEmail())) {
        if (!decodedToken.getClaims().containsKey(RoleConstants.ROLE_SUPER)) {
          try {
            roleService.addRole(decodedToken.getUid(), RoleConstants.ROLE_SUPER);
          } catch (Exception e) {
            log.error("Super Role registration exception ", e);
          }
        }
        authorities.add(new SimpleGrantedAuthority(RoleConstants.ROLE_SUPER));
      }
      // Handle other roles
      decodedToken.getClaims().forEach((k, v) -> authorities.add(new SimpleGrantedAuthority(k)));

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          user,
          new Credentials(decodedToken, token), authorities);
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
  }

  private User firebaseTokenToUserDto(FirebaseToken decodedToken) {
    Optional<User> user = Optional.empty();

    if (decodedToken != null) {
      user = userRepository.findByUid(decodedToken.getUid());
    }

    return user.orElse(null);
  }

}