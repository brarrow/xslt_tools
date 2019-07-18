package screenform;

import console.Console;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class JDOMFunctions {
    public static Document useSAXParser(String fileName) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();

            return saxBuilder.build(new File(fileName));
        } catch (Exception ignored) {
            Console.printMessage("Error: while parsing xslt document.", Console.ANSI_RED);
        }
        return null;
    }

    public static Element findElWithNameAndCont(Element root, String contains, String name) {
        for (Content el : root.getDescendants()) {
            try {
                if (el instanceof Text) {
                    if (((Text) el).getText().contains(contains) & name.contentEquals("Text")) {
                        return el.getParentElement();
                    }
                } else if (el instanceof Element) {
                    if (el.getValue().contains(contains) & (((Element) el).getName().contains(name))) {
                        return (Element) el;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Element findElWithNameAndAttr(Element root, String attributeName, String attributeValue, String name) {
        for (Content el : root.getDescendants()) {
            try {
                if (((Element) el).getAttribute(attributeName).getValue().contains(attributeValue) & (((Element) el).getName().contains(name))) {
                    return (Element) el;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static Integer getLineNumberOfElement(Element element, String xslt_string) {
        List<String> xslt_rows = Arrays.asList(xslt_string.split("\n"));
        String elementName = element.getName();
        List<Attribute> elementAttrs = element.getAttributes();
        for (int i = 0; i < xslt_rows.size(); i++) {
            String row = xslt_rows.get(i);
            if (row.contains(elementName) & row.contains("<")) {
                boolean finded = true;
                String tmpRow = row;
                String elementHead = tmpRow;
                int posTmpRow = i;
                while (!tmpRow.contains(">")) {
                    elementHead = elementHead.concat("\n" + xslt_rows.get(++posTmpRow));
                    tmpRow = xslt_rows.get(posTmpRow);
                }

                elementHead = getNormalLine(elementHead);
                elementHead = elementHead.substring(0, elementHead.indexOf(">"));


                for (Attribute attribute : elementAttrs) {
                    if (!elementHead.contains(attribute.getName())) finded = false;
                    if (!elementHead.contains(getNormalLine(attribute.getValue()))) finded = false;
                }
                if (finded) {
                    return Integer.valueOf(i + 1);
                }
            }
        }
        return Integer.valueOf(-1);
    }

    public static String getNormalLine(String line) {
        return line.replaceAll("\n", " ").replaceAll("\r", " ")
                .replaceAll("\t", " ").replaceAll("\\s+", " ");

    }
}
