package com.halildurmus.hotdeals.user.dummy;

import com.halildurmus.hotdeals.user.User;

public class DummyUsers {

  public static User user1 = new User("11111111", "mike@spring.io", "mike1234",
      "http://www.gravatar.com/avatar");
  public static User user2 = new User("22222222", "duke@spring.io", "duke1234",
      "http://www.gravatar.com/avatar");
  public static User user3 = new User("33333333", "hannah@spring.io", "hannah1234",
      "http://www.gravatar.com/avatar");
  public static User user4 = new User("44444444", "harold@spring.io", "harold1234",
      "http://www.gravatar.com/avatar");
  public static User user4WithoutNickname = new User("55555555", "harold@spring.io", "",
      "http://www.gravatar.com/avatar");

}
