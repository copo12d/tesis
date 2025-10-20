package com.tesisUrbe.backend.reportsManagerPdf.builders;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RequiredArgsConstructor
public class PdfReportBuilder<T> implements ReportBuilder<T> {

    private final PdfHeaderBuilder headerBuilder;
    private final PdfFooterBuilder footerBuilder;
    private final PdfTableBuilderInterface<T> tableBuilder;

    public byte[] build(String reportTitle, List<String> columnTitles, List<T> records, String username) throws Exception {
        Document doc = new Document(PageSize.A4, 36, 36, 36, 60);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(doc, out);
        writer.setPageEvent(footerBuilder);
        doc.open();
        headerBuilder.build(doc, reportTitle, username);
        tableBuilder.build(doc, records, columnTitles);
        doc.close();
        return out.toByteArray();
    }

}
