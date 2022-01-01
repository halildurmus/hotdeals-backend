package com.halildurmus.hotdeals.util;

/**
 * A utility class with useful function(s) to make it easier to work with {@code Enum}s.
 */
public class EnumUtil {

  /**
   * Checks whether the given {@code value} exists in the given {@code enumClass}.
   *
   * @param value     the {@code enum} value to be tested
   * @param enumClass the {@code enum Class} to be checked eq. {@code DealStatus.class}
   * @return {@code true} if the {@code value} exists in the {@code enumClass}; {@code false}
   * otherwise.
   */
  public static <E extends Enum<E>> boolean isInEnum(String value, Class<E> enumClass) {
    for (E e : enumClass.getEnumConstants()) {
      if (e.name().equals(value)) {
        return true;
      }
    }

    return false;
  }

}
