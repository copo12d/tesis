package com.tesisUrbe.backend.reportsManagerPdf.builders;

import com.lowagie.text.Document;

import java.util.List;

public interface PdfTableBuilderInterface<T> {
    void build(Document doc, List<T> records, List<String> columnTitles) throws Exception;
}
