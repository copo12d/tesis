package com.tesisUrbe.backend.reports.builders;

import java.util.List;

public interface ReportBuilder<T> {
    byte[] build(String reportTitle, List<String> columnTitles, List<T> records, String username) throws Exception;
}
