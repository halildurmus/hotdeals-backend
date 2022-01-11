package com.halildurmus.hotdeals.user.dummy;

import com.halildurmus.hotdeals.user.User;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DummyUsers {

  public static User user1 = User.builder().id("5fbe790ec6f0b32014074bb1")
      .uid("432hdf5324sf21").avatar("http://www.gravatar.com/avatar")
      .nickname("mike1234").email("mike@spring.io")
      .favorites(new HashSet<>(List.of("5fbe790ec2f0b32014074bb5")))
      .blockedUsers(new HashSet<>(List.of("5fbe790ec6f0b32014074bb2")))
      .fcmTokens(Map.of("423hf73", "4fsafh2hjgfsaf234hfhfads"))
      .createdAt(Instant.now()).updatedAt(Instant.now()).build();

  public static User patchedUser1 = User.builder().id("5fbe790ec6f0b32014074bb1")
      .uid("432hdf5324sf21").avatar("http://www.gravatar.com/avatarNew")
      .nickname("mike1234New").email("mike@spring.io")
      .favorites(new HashSet<>(List.of("5fbe790ec2f0b32014074bb5")))
      .blockedUsers(new HashSet<>(List.of("5fbe790ec6f0b32014074bb2")))
      .fcmTokens(Map.of("423hf73", "4fsafh2hjgfsaf234hfhfads"))
      .createdAt(Instant.now()).updatedAt(Instant.now()).build();

  public static User user2 = User.builder().id("5fbe790ec6f0b32014074bb2")
      .uid("432hdf5324sf22").avatar("http://www.gravatar.com/avatar")
      .nickname("duke1234").email("duke@spring.io")
      .favorites(new HashSet<>(List.of("5fbe790ec2f0b32014074bb5")))
      .blockedUsers(new HashSet<>(List.of("5fbe790ec6f0b32014074bb3")))
      .fcmTokens(Map.of("423hf73", "4fsafh2hjgfsaf234hfhfads2"))
      .createdAt(Instant.now()).updatedAt(Instant.now()).build();

  public static User user3 = User.builder().id("5fbe790ec6f0b32014074bb3")
      .uid("432hdf5324sf23").avatar("http://www.gravatar.com/avatar")
      .nickname("finch1234").email("finch@spring.io")
      .favorites(new HashSet<>(List.of("5fbe790ec2f0b32014074bb5")))
      .blockedUsers(new HashSet<>(List.of("5fbe790ec6f0b32014074bb1")))
      .fcmTokens(Map.of("423hf73", "4fsafh2hjgfsaf234hfhfads3"))
      .createdAt(Instant.now()).updatedAt(Instant.now()).build();

}
