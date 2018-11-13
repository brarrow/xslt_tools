package printform;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;

public class Xsd {
    private static org.jdom2.Document useSAXParser(String fileName) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            return saxBuilder.build(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void openXsd(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        root.getAttributeValue("lkj");
    }

}
