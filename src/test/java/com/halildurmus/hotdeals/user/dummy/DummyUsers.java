package com.halildurmus.hotdeals.user.dummy;

import com.halildurmus.hotdeals.user.User;

public class DummyUsers {

  public static User user1 = User.builder().id("11111111").email("mike@spring.io")
      .nickname("mike1234").avatar("http://www.gravatar.com/avatar").build();
  public static User user2 = User.builder().id("22222222").email("duke@spring.io")
      .nickname("duke1234").avatar("http://www.gravatar.com/avatar").build();
  public static User user3 = User.builder().id("33333333").email("hannah@spring.io")
      .nickname("hannah1234").avatar("http://www.gravatar.com/avatar").build();
  public static User user4 = User.builder().id("44444444").email("harold@spring.io")
      .nickname("harold1234").avatar("http://www.gravatar.com/avatar").build();
  public static User user4WithoutNickname = User.builder().id("55555555")
      .email("harold@spring.io").nickname("").avatar("http://www.gravatar.com/avatar").build();

}
