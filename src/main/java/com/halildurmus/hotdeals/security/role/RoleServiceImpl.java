package com.halildurmus.hotdeals.security.role;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

  @Autowired
  private FirebaseAuth firebaseAuth;

  @Override
  public void addRole(String uid, String role) {
    try {
      UserRecord user = firebaseAuth.getUser(uid);
      Map<String, Object> claims = new HashMap<>(user.getCustomClaims());
      claims.putIfAbsent(role, true);
      firebaseAuth.setCustomUserClaims(uid, claims);
    } catch (FirebaseAuthException e) {
      log.error("Firebase Auth Exception", e);
    }
  }

  @Override
  public void removeRole(String uid, String role) {
    try {
      UserRecord user = firebaseAuth.getUser(uid);
      Map<String, Object> claims = new HashMap<>(user.getCustomClaims());
      claims.remove(role);
      firebaseAuth.setCustomUserClaims(uid, claims);
    } catch (FirebaseAuthException e) {
      log.error("Firebase Auth Exception", e);
    }
  }

}