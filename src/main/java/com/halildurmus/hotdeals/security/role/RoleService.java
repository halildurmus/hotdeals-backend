package com.halildurmus.hotdeals.security.role;

public interface RoleService {

  void add(String uid, String role);

  void delete(String uid, String role);

}