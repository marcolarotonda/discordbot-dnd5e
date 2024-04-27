package io.github.marcolarotonda.discordbotdnd5e.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {

    private StringUtils() {}
    public static final String BLOCK_DELIMITER = "```";
    public static final String BOLD_DELIMITER = "**";

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return Arrays.stream(str.split("\\s+"))
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining(" "));
    }
}
