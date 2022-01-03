package com.halildurmus.hotdeals.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class FakerUtilTest {

  @Test
  public void shouldGenerateRandomNickname() {
    final FakerUtil fakerUtil = new FakerUtil();
    final String nickname1 = fakerUtil.generateNickname();

    assertFalse(nickname1.isBlank());
    assertThat(nickname1).hasSizeGreaterThanOrEqualTo(8);

    final String nickname2 = fakerUtil.generateNickname();

    assertFalse(nickname2.isBlank());
    assertThat(nickname2).hasSizeGreaterThanOrEqualTo(8);
    assertThat(nickname2).isNotEqualTo(nickname1);
  }

}
