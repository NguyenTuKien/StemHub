package com.team7.StemHub.util;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import java.io.*;

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
}
