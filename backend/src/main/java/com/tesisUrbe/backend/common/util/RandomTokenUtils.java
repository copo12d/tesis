package com.tesisUrbe.backend.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomTokenUtils {

    public static String randomToken(String prefix) {
        String uuid = UUID.randomUUID().toString();

        String tokenData = prefix + uuid;

        return Base64.getUrlEncoder().encodeToString(tokenData.getBytes(StandardCharsets.UTF_8));
    }
}
