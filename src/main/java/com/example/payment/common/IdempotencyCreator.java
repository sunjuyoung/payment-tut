package com.example.payment.common;

import java.util.UUID;

public class IdempotencyCreator {

    public static String create(String data) {
        return UUID.fromString(data).toString();
    }
}
