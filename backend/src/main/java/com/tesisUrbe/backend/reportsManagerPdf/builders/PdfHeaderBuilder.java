package com.tesisUrbe.backend.reportsManagerPdf.builders;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tesisUrbe.backend.reportsManagerPdf.config.UniversityConfig;
import lombok.RequiredArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@RequiredArgsConstructor
public class PdfHeaderBuilder {

    private final UniversityConfig university;

    public void build(Document doc, PdfWriter writer, String title, String username) throws Exception {
        PdfPTable outerTable = new PdfPTable(1);
        outerTable.setWidthPercentage(100);
        outerTable.setSpacingAfter(10f);

        PdfPTable innerTable = new PdfPTable(2);
        innerTable.setWidths(new float[]{1f, 4f});
        innerTable.setWidthPercentage(100);
        innerTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        Image logo = Image.getInstance(getClass().getResourceAsStream("/static" + university.getLogoPath()).readAllBytes());
        logo.scaleToFit(80, 80);
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        innerTable.addCell(logoCell);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

        Paragraph info = new Paragraph();
        info.setAlignment(Element.ALIGN_CENTER);
        info.setLeading(12f);
        info.add(new Phrase(university.getLegalName() + "\n", titleFont));
        info.add(new Phrase("RIF: " + university.getTaxId().getType() + "-" + university.getTaxId().getNumber() + "\n", titleFont));
        info.add(new Phrase(university.getAddress_1() + "\n", normalFont));
        info.add(new Phrase(university.getAddress_2() + "\n", normalFont));
        info.add(new Phrase(university.getAddress_3() + "\n", normalFont));
        info.add(new Phrase(university.getPhone() + "\n", normalFont));
        info.add(new Phrase(university.getEmail() + "\n\n", normalFont));
        info.add(new Phrase(title + "\n\n", titleFont));

        Paragraph signature = new Paragraph();
        signature.setAlignment(Element.ALIGN_RIGHT);
        signature.setLeading(10f);
        signature.add(new Phrase("Generado por: " + username + "\n", smallFont));
        signature.add(new Phrase("Fecha: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "\n", smallFont));

        PdfPCell infoCell = new PdfPCell();
        infoCell.addElement(info);
        infoCell.addElement(signature);
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        infoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        infoCell.setPaddingLeft(-80f);
        infoCell.setPaddingTop(10f);
        infoCell.setPaddingBottom(10f);
        innerTable.addCell(infoCell);

        PdfPCell wrapperCell = new PdfPCell(innerTable);
        wrapperCell.setBorder(Rectangle.NO_BORDER);
        wrapperCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        outerTable.addCell(wrapperCell);

        doc.add(outerTable);
        doc.add(Chunk.NEWLINE);
    }

}
