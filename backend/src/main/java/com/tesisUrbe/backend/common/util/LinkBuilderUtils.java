package com.tesisUrbe.backend.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LinkBuilderUtils {

    public static String buildEmailLink(String domain, String apiVersion, String pathSegment, Long userId, String token) {
        return UriComponentsBuilder.fromUriString(domain)
                .path(apiVersion + "/email/public/" + pathSegment + "/" + userId + "/" + token)
                .toUriString();
    }
}
