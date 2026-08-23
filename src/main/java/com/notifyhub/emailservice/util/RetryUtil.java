package com.notifyhub.emailservice.util;

public final class RetryUtil {

    private RetryUtil() {
    }

    public static long getDelay(int retryCount) {

        return switch (retryCount) {

            case 0 -> 30;

            case 1 -> 120;

            case 2 -> 300;

            default -> 900;
        };
    }
}
