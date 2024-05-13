package org.example.common.util;

public class MathUtil {

    public static int ceil(long a, long c) {
        final double ceil = Math.ceil(a * 1.0 / c);
        return Math.toIntExact((long) ceil);
    }

}
