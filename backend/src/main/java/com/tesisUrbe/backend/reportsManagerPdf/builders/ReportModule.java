package com.tesisUrbe.backend.reportsManagerPdf.builders;

public record ReportModule<T>(RowMapper<T> mapper, PdfTableBuilderInterface<T> tableBuilder,
                              ReportBuilder<T> reportBuilder) {
}
