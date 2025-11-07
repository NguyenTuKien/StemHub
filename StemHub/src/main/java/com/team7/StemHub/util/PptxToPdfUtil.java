package com.team7.StemHub.util;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PptxToPdfUtil {

    public static void convertPptxToPdf(File pptxFile, File pdfFile) throws Exception {
        // 1. Nạp file PPTX
        try (FileInputStream fis = new FileInputStream(pptxFile);
             XMLSlideShow ppt = new XMLSlideShow(fis);
             FileOutputStream fos = new FileOutputStream(pdfFile)) {

            // 2. Chuẩn bị PDF document
            Document pdf = new Document();
            PdfWriter.getInstance(pdf, fos);
            pdf.open();

            Dimension pgsize = ppt.getPageSize();

            // 3. Vẽ từng slide và thêm vào PDF
            for (XSLFSlide slide : ppt.getSlides()) {
                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = img.createGraphics();
                graphics.setPaint(Color.white);
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

                // Render slide
                slide.draw(graphics);
                graphics.dispose();

                // Ghi ảnh slide vào PDF
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "png", baos);
                Image slideImage = Image.getInstance(baos.toByteArray());
                slideImage.scaleToFit(pdf.getPageSize().getWidth(), pdf.getPageSize().getHeight());
                pdf.add(slideImage);
            }

            pdf.close();
        }
    }

    public static MultipartFile convertPptxToPdf(MultipartFile pptxFile) throws Exception {
        String original = pptxFile.getOriginalFilename();
        String pdfName = (original != null ? original.replaceAll("(?i)\\.pptx$", ".pdf") : "converted.pdf");

        Path tmpPptx = Files.createTempFile("pptx_in_", ".pptx");
        Path tmpPdf = Files.createTempFile("pptx_out_", ".pdf");
        try {
            Files.write(tmpPptx, pptxFile.getBytes());
            convertPptxToPdf(tmpPptx.toFile(), tmpPdf.toFile());
            byte[] pdfBytes = Files.readAllBytes(tmpPdf);
            return new MultipartFileUtil("file", pdfName, "application/pdf", pdfBytes);
        } finally {
            try { Files.deleteIfExists(tmpPptx);} catch (Exception ignored) {}
            try { Files.deleteIfExists(tmpPdf);} catch (Exception ignored) {}
        }
    }
}
