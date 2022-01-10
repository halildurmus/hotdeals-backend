package com.halildurmus.hotdeals.user.dummy;

import com.halildurmus.hotdeals.user.User;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DummyUsers {

  public static User user1 = User.builder().id("5fbe790ec6f0b32014074bb1")
      .uid("432hdf5324sf23").avatar("http://www.gravatar.com/avatar")
      .nickname("mike1234").email("mike@spring.io")
      .blockedUsers(new HashSet<>(List.of("5fbe790ec6f0b32014074bb2")))
      .fcmTokens(Map.of("423hf73", "4fsafh2hjgfsaf234hfhfads"))
      .createdAt(Instant.now()).build();
  public static User patchedUser1 = User.builder().id("5fbe790ec6f0b32014074bb1")
      .uid("432hdf5324sf23").avatar("http://www.gravatar.com/avatarNew")
      .nickname("mike1234New").email("mike@spring.io")
      .blockedUsers(new HashSet<>(List.of("5fbe790ec6f0b32014074bb2")))
      .fcmTokens(Map.of("423hf73", "4fsafh2hjgfsaf234hfhfads"))
      .createdAt(Instant.now()).build();
  public static User user2 = User.builder().id("5fbe790ec6f0b32014074bb2")
      .email("duke@spring.io").nickname("duke1234")
      .avatar("http://www.gravatar.com/avatar").build();
  public static User user3 = User.builder().id("5fbe790ec6f0b32014074bb3")
      .email("hannah@spring.io").nickname("hannah1234")
      .avatar("http://www.gravatar.com/avatar").build();
  public static User user4 = User.builder().id("5fbe790ec6f0b32014074bb4")
      .uid("432hdf5324sf23").avatar("http://www.gravatar.com/avatar")
      .nickname("harold1234").createdAt(Instant.now()).build();
  public static User user4WithoutNickname = User.builder().uid("432hdf5324sf23")
      .email("harold@spring.io").avatar("http://www.gravatar.com/avatar").build();

}
