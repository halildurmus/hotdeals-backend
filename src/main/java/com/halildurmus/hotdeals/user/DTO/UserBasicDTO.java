package com.halildurmus.hotdeals.user.DTO;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserBasicDTO {

  private final String id;

  private final String uid;

  private final String avatar;

  private final String nickname;

  private final Instant createdAt;

}
