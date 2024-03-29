package com.halildurmus.hotdeals.security.role;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

  @Autowired private FirebaseAuth firebaseAuth;

  @Override
  public void add(String uid, Role role) {
    try {
      var user = firebaseAuth.getUser(uid);
      Map<String, Object> claims = new HashMap<>(user.getCustomClaims());
      claims.putIfAbsent(role.name(), true);
      firebaseAuth.setCustomUserClaims(uid, claims);
    } catch (FirebaseAuthException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Firebase Auth Exception", e);
    }
  }

  @Override
  public void delete(String uid, Role role) {
    try {
      var user = firebaseAuth.getUser(uid);
      Map<String, Object> claims = new HashMap<>(user.getCustomClaims());
      claims.remove(role.name());
      firebaseAuth.setCustomUserClaims(uid, claims);
    } catch (FirebaseAuthException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Firebase Auth Exception", e);
    }
  }
}
