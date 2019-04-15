package screenform;

import files.FilesIO;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class Processing {
    private static List<String> rows;

    static void processXSLT() {
        try {
            readRows(FilesIO.input);
            zero();
//            addMD5Comment();
            tabToWhite();
            FilesIO.writeToFile(rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void readRows(String filePath) {
        try {
            List<String> listRows = Files.readAllLines(new File(filePath).toPath(), Charset.forName("UTF-8"));
            setRows(listRows);
        } catch (Exception e) {
            System.out.println("Error while reading file!");
        }
    }

    private static void setRows(List<String> listRows) {
        rows = listRows;
    }

    static String deleteAllNonCharacter(String str) {
        return str.replace(":", "").replace(".", "");
    }

    private static void zero() {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).trim().length() == 0)
                rows.remove(i--);
        }
    }

    private static void addMD5Comment() {
        try (InputStream is = Files.newInputStream(Paths.get(FilesIO.input))) {
            String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
            rows.add(5, "<!--" + "md5:" + md5 + "-->");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            rows.add(5, "<!--" + "generated:" + dtf.format(now) + "-->");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void tabToWhite() {
        for (int i = 0; i < rows.size(); i++) {
            rows.set(i, rows.get(i).replaceAll("\\t", " "));
        }
    }
}
