package com.team7.StemHub.util;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class DocxToPdfUtil {

    public static void convertDocxToPdf(String docxPath, String pdfPath) {
        try {
            // 1. Tải file DOCX
            File docxFile = new File(docxPath);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(docxFile);

            // 2. Chuẩn bị file output PDF
            OutputStream os = new FileOutputStream(pdfPath);

            // 3. Thực hiện chuyển đổi
            Docx4J.toPDF(wordMLPackage, os);

            // 4. Đóng file
            os.flush();
            os.close();

            System.out.println("Chuyển đổi thành công!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MultipartFile convertDocxToPdf(MultipartFile docxFile) throws IOException {
        String original = docxFile.getOriginalFilename();
        String pdfName = (original != null ? original.replaceAll("(?i)\\.docx$", ".pdf") : "converted.pdf");

        Path tmpDocx = Files.createTempFile("docx_in_", ".docx");
        Path tmpPdf = Files.createTempFile("docx_out_", ".pdf");
        try {
            Files.write(tmpDocx, docxFile.getBytes());
            convertDocxToPdf(tmpDocx.toString(), tmpPdf.toString());
            byte[] pdfBytes = Files.readAllBytes(tmpPdf);
            return new MultipartFileUtil("file", pdfName, "application/pdf", pdfBytes);
        } finally {
            try { Files.deleteIfExists(tmpDocx);} catch (Exception ignored) {}
            try { Files.deleteIfExists(tmpPdf);} catch (Exception ignored) {}
        }
    }
}
