package com.tesisUrbe.backend.reportsManagerPdf.builders;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.tesisUrbe.backend.common.constants.CommonConstants;
import com.tesisUrbe.backend.reportsManagerPdf.config.ReportStyleConfig;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class PdfFooterBuilder extends PdfPageEventHelper {

    private final ReportStyleConfig config;

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
        Rectangle pageSize = document.getPageSize();

        float centerX = (pageSize.getLeft() + pageSize.getRight()) / 2;
        float bottomY = pageSize.getBottom() + 20;

        PdfContentByte canvas = writer.getDirectContent();
        canvas.beginText();
        canvas.setFontAndSize(footerFont.getBaseFont(), 10);

        canvas.showTextAligned(Element.ALIGN_CENTER, CommonConstants.Author, centerX, bottomY + 20, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, "Todos los derechos reservados © " + LocalDateTime.now().getYear(), centerX, bottomY + 10, 0);
        canvas.showTextAligned(Element.ALIGN_CENTER, "Página " + writer.getPageNumber(), centerX, bottomY, 0);

        canvas.endText();
    }
}
