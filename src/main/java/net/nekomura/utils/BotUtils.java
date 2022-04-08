package net.nekomura.utils;

import java.util.Arrays;

public class BotUtils {
    public static boolean isApplyGroup(long group) {
        long[] apply = {1079412135, 1021208966};
        return Arrays.stream(apply).anyMatch(x -> x == group);
    }
}
