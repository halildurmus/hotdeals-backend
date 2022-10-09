package com.halildurmus.hotdeals.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class FakerUtilTest {

  @Test
  public void generatesRandomNickname() {
    final FakerUtil fakerUtil = new FakerUtil();
    final String nickname = fakerUtil.generateNickname();

    assertThat(nickname).hasSizeGreaterThanOrEqualTo(8);
    assertThat(nickname).containsPattern(Pattern.compile("^\\w+\\d{3}$"));
  }
}
