package com.halildurmus.hotdeals.security.role;

public interface RoleService {

  void addRole(String uid, String role);

  void removeRole(String uid, String role);

}