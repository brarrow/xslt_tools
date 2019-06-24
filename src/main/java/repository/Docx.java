package repository;

import files.FilesIO;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import webstand.cases.CasesFunctions;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.stream.Collectors;

public class Docx {
    public static void openDocWithCase(String caseName) {
        File file = new File(getDocxPath(caseName));
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ignored) {
        }

    }

    public static String getOptName(String caseName) {
        try {
            FileInputStream fis = new FileInputStream(getDocxPath(caseName));
            XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
            String docStr = xdoc.getDocument().toString();
            int startPos = docStr.indexOf("openEHR-EHR-COMPOSITION");
            int endPos = startPos + docStr.substring(startPos).indexOf("</w:t>");
            String optName = docStr.substring(startPos, endPos);
            return optName;
        } catch (Exception ignored) {
            System.out.println("Error while getting optName from Docx.");
        }
        return null;
    }

    private static String getDocxPath(String caseName) {
        String path = CasesFunctions.findPathWithCase(caseName);
        int dirPos = path.indexOf(FilesIO.delim + "xslt");
        path = path.substring(0, dirPos);
        path = path.concat(FilesIO.delim + "doc");

        final File folder = new File(path);
        return path + FilesIO.delim + FilesIO.listFilesForFolder(folder).stream()
                .filter(el -> el.toLowerCase().endsWith(".docx"))
                .filter(el -> !(el.toLowerCase().contains("~")))
                .collect(Collectors.joining());
    }
}
