package com.numarics.player;

import java.util.SplittableRandom;

public class TestUtil {

    public static Long generateId() {
        return new SplittableRandom().nextLong(0, 1_001);
    }
}
