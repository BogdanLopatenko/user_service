package com.user_service.constant;


import java.time.Instant;

public final class ConstantTest {

    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-15T00:00:00Z");

    public static final Instant INSTANT_BEFORE = Instant.parse("2025-01-10T00:00:00Z");

    public static final Instant INSTANCE_AFTER = Instant.parse("2025-01-20T00:00:00Z");



    private ConstantTest() {
    }
}
