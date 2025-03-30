package com.sintergica.apiv2.utilidades;
import java.security.SecureRandom;
import java.time.Instant;

public final class KeyGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final String BASE36 = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static String generateIdLong() {

        long timestamp = Instant.now().toEpochMilli() & 0xFFFFFFFFL;
        long randomLong = random.nextLong();

        long combined = (timestamp << 64) | (randomLong & 0xFFFFFFFFFFFFL);
        return base36Encode(combined).substring(0, 12);
    }

    private static String base36Encode(long number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.insert(0, BASE36.charAt((int) (number % 36)));
            number = number / 36;
        }
        return result.toString();
    }

    public static String generateShortId() {
        long value = (System.currentTimeMillis() << 20) | (random.nextInt() & 0xFFFFF);
        return base36Encode(value).substring(0, 8);
    }

}