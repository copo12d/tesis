package com.tesisUrbe.backend.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomTokenUtils {

    /**
     * Genera un token único y codificado en Base64. El prefijo se incluye
     * en la cadena que se codifica, haciéndolo indistinguible a simple vista.
     *
     * @param prefix El prefijo String para el token, para asegurar su unicidad.
     * @return Un String que representa un token único y codificado.
     */
    public static String randomToken(String prefix){
        String uuid = UUID.randomUUID().toString();

        String tokenData = prefix + uuid;

        return Base64.getUrlEncoder().encodeToString(tokenData.getBytes(StandardCharsets.UTF_8));
    }
}
