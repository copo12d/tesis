package com.tesisUrbe.backend.prediction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NextRecollectionDto(
        Long id,
        String containerSerial,
        String nextRecollectionTime
) {
}
