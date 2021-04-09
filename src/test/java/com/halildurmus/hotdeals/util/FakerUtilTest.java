package com.halildurmus.hotdeals.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class FakerUtilTest {

  @Test
  public void shouldGenerateNickname() {
    FakerUtil fakerUtil = new FakerUtil();
    String nickname1 = fakerUtil.generateNickname();

    assertFalse(nickname1.isBlank());
    assertThat(nickname1).hasSizeGreaterThan(8);

    String nickname2 = fakerUtil.generateNickname();

    assertFalse(nickname2.isBlank());
    assertThat(nickname2).isNotEqualTo(nickname1);
    assertThat(nickname2).hasSizeGreaterThan(8);
  }

}
