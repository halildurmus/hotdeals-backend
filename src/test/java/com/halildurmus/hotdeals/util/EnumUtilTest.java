package com.halildurmus.hotdeals.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EnumUtilTest {

  @Test
  public void shouldCheckWhetherTheGivenValueExistsInTheGivenEnumClass() {
    assertTrue(EnumUtil.isInEnum("ACTIVE", Status.class));
    assertTrue(EnumUtil.isInEnum("DISABLED", Status.class));
    assertFalse(EnumUtil.isInEnum("NOTEXISTS", Status.class));
  }

  public enum Status {
    ACTIVE, DISABLED
  }

}
