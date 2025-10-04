package com.tesisUrbe.backend.reportsManagerPdf.builders;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ReportModule<T> {
    private final RowMapper<T> mapper;
    private final PdfTableBuilderInterface<T> tableBuilder;
    private final ReportBuilder<T> reportBuilder;
}
