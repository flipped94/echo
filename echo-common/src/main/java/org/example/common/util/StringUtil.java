package org.example.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StringUtil {

    public static String generateRandomString(int length) {
        if (length < 3) {
            throw new RuntimeException("Length must be at least 3 to include lowercase, uppercase, and a digit.");
        }

        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        // 确保大小写数字
        sb.append('a');
        sb.append('A');
        sb.append('0');
        sb.append('/');
        sb.append('.');

        // 填充
        int remainingLength = length - 3;
        String allChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 0; i < remainingLength; i++) {
            sb.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 打乱
        return shuffleString(sb.toString(), random);
    }

    private static String shuffleString(String string, Random random) {
        List<Character> characters = new ArrayList<>(string.length());
        for (char c : string.toCharArray()) {
            characters.add(c);
        }

        Collections.shuffle(characters, random);

        StringBuilder shuffled = new StringBuilder(string.length());
        for (char c : characters) {
            shuffled.append(c);
        }

        return shuffled.toString();
    }

    public static String numberCode(int length) {
        Random random = new Random();
        final StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(9));
        }
        return builder.toString();
    }
}
