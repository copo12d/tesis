package com.tesisUrbe.backend.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LinkBuilderUtils {

    public static String buildEmailLink(String domain, String pathSegment, Long userId) {
        return UriComponentsBuilder.fromUriString(domain)
                .path("/email/public/" + pathSegment + "/" + userId + "/token")
                .toUriString();
    }
}
