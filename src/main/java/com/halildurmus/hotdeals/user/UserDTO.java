package com.halildurmus.hotdeals.user;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO implements Serializable {

  private static final long serialVersionUID = 1234567L;

  private String id;

  private String uid;

  private String avatar;

  private String nickname;

}
