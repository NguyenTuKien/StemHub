package com.team7.StemHub.util;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

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
}
