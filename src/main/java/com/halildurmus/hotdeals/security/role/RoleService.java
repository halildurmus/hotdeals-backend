package com.halildurmus.hotdeals.security.role;

public interface RoleService {

  void addRole(String uid, String role);

  void deleteRole(String uid, String role);

}