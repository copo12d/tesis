package com.tesisUrbe.backend.reports.builders;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.tesisUrbe.backend.reports.config.ReportStyleConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;

@AllArgsConstructor
public class PdfTableBuilder<T> implements PdfTableBuilderInterface<T> {

    private final ReportStyleConfig config;
    private final RowMapper<T> rowMapper;

    @Override
    public void build(Document doc, List<T> records, List<String> columnTitles) throws Exception {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPTable table = new PdfPTable(columnTitles.size());
        table.setWidthPercentage(100);

        for (String title : columnTitles) {
            PdfPCell cell = new PdfPCell(new Phrase(title, headerFont));
            cell.setBackgroundColor(Color.decode("#" + config.getHeaderColor()));
            table.addCell(cell);
        }

        for (T record : records) {
            List<String> values = rowMapper.mapRow(record);
            for (String value : values) {
                table.addCell(new Phrase(value, cellFont));
            }
        }

        doc.add(table);
    }
}
