package testing;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {
    public static String getHtmlText(String input) {
        try {
            String text = Files.readString(Paths.get(input));
            // replace all occurrences of one or more HTML tags with optional
            return text.substring(text.indexOf("tbody") + 6)
                    .replaceAll("<strong>*</strong>", " ")
                    .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ")
                    .replaceAll("\\{([^}]*)}", " ");
        } catch (Exception ex) {
            System.out.println("TESTING: Error while reading html file.");
            return null;
        }
    }


}
