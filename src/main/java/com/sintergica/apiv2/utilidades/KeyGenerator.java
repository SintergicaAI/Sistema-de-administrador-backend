package com.sintergica.apiv2.utilidades;

import java.security.SecureRandom;
import java.time.Instant;

/**
 * KeyGenerator class used to generate unique keys
 *
 * @author Javier Palacios
 * 02/03/2025
 */

public final class KeyGenerator {
  private static final SecureRandom random = new SecureRandom();
  private static final String BASE36 = "0123456789abcdefghijklmnopqrstuvwxyz";

  /**
   * Generates a unique identifier as a base36 encoded string.
   * The identifier is constructed by combining the current timestamp
   * and a random long value, ensuring uniqueness.
   * The resulting string is truncated to 12 characters.
   *
   * @return A 12-character base36 encoded string representing the unique identifier.
   */
  public static String generateIdLong() {

    long timestamp = Instant.now().toEpochMilli() & 0xFFFFFFFFL;
    long randomLong = random.nextLong();

    long combined = (timestamp << 64) | (randomLong & 0xFFFFFFFFFFFFL);
    return base36Encode(combined).substring(0, 12);
  }

  /**
   * Encodes a given long number into a base36 string representation.
   * This method converts the input number into a string using the
   * characters defined in the BASE36 constant, which includes
   * digits and lowercase letters.
   *
   * @param number The long number to be encoded.
   * @return A string representing the base36 encoded value of the input number.
   */
  private static String base36Encode(long number) {
    StringBuilder result = new StringBuilder();
    while (number > 0) {
      result.insert(0, BASE36.charAt((int) (number % 36)));
      number = number / 36;
    }
    return result.toString();
  }

  /**
   * Generates a short identifier as a base36 encoded string.
   * The identifier is constructed by shifting the current timestamp
   * 20 bits to the left and combining it with a random integer
   * between 0 and 1048575 (0xFFFFF). The resulting string is
   * truncated to 8 characters, resulting in a unique short identifier.
   *
   * @return An 8-character base36 encoded string representing the unique identifier.
   */
  public static String generateShortId() {
    long value = (System.currentTimeMillis() << 20) | (random.nextInt() & 0xFFFFF);
    return base36Encode(value).substring(0, 8);
  }
}
