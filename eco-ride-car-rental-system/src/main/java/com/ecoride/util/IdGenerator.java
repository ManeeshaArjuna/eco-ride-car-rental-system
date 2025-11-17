package com.ecoride.util;

import java.util.UUID;

public class IdGenerator {
    public static String shortId() {
        return UUID.randomUUID().toString().substring(0,8);
    }
}
