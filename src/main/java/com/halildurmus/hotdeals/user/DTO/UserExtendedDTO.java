package com.halildurmus.hotdeals.user.DTO;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserExtendedDTO {

  private final String id;

  private final String uid;

  private final String avatar;

  private final String nickname;

  private final Map<String, Boolean> blockedUsers;

  private final Map<String, String> fcmTokens;

  private final Instant createdAt;

}
