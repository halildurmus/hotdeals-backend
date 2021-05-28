package com.halildurmus.hotdeals.user;

public interface UserService {

  User create(User user);

  User block(String userId) throws Exception;

  User unblock(String userId) throws Exception;

}