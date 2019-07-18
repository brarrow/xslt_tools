package testing;

import console.Console;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import repository.Git;
import screenform.JDOMFunctions;
import webstand.cases.CasesFunctions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    static String xslt_text;
    static Document xslt_doc;
    static Element xslt_root;
    static List<String> bugsIf;
    static String lineBreak = "-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-";

    public static String getHtmlText(String filePath) {
        try {
            String text = Files.readString(Paths.get(filePath));
            // replace all occurrences of one or more HTML tags with optional
            return text.substring(text.indexOf("tbody") + 6)
                    .replaceAll("<strong>*</strong>", " ")
                    .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ")
                    .replaceAll("\\{([^}]*)}", " ");
        } catch (Exception ignored) {
            System.out.println("TESTING: Error while reading html file.");
            return null;
        }
    }

    private static void getXsltText(String filePath) {
        try {
            xslt_text = Files.readString(Paths.get(filePath));
        } catch (Exception ignored) {
            System.out.println("TESTING: Error while reading xslt file.");
        }
    }

    private static void getXsltDom(String filePath) {
        try {
            xslt_doc = JDOMFunctions.useSAXParser(filePath);
            xslt_root = xslt_doc.getRootElement();
        } catch (Exception ignored) {
            System.out.println("TESTING: Error while reading DOM of xslt file.");
        }
    }

    public static void testAllXslt() {
        List<String> bugs = new ArrayList<>();
        int bugsCount = 0;
        int buggedTemplates = 0;
        int checked_cases = 0;

        for (String caseName : CasesFunctions.getAllCases()) {
            bugs = testXslt(caseName);
            checked_cases++;

            bugsCount += bugs.size();
            if (bugs.size() != 0) buggedTemplates++;
        }
        Console.printMessage("Total: " + bugsCount + " bugs, " + "in " + buggedTemplates + " templates! Percent: " + roundAvoid((double) buggedTemplates / checked_cases * 100, 2)
                , Console.ANSI_RED);

    }

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static List<String> testXslt(String caseName) {
        String filePath = CasesFunctions.findPathWithCase(caseName);
        List<String> bugs = new ArrayList<>();
        List<String> tmpBugs;
        getXsltText(filePath);
        getXsltDom(filePath);

        getTextLineBreak("Testing. Template info.");
        Console.printMessage("Path: ", Console.ANSI_BLUE, false);
        System.out.println(CasesFunctions.findPathWithCase(caseName));

        Console.printMessage("Case on stand: ", Console.ANSI_BLUE, false);
        System.out.println(caseName);

        Console.printMessage("Last commit by: ", Console.ANSI_BLUE, false);
        System.out.println(Git.getCaseLastCommitAuthor(caseName));

        Console.printMessage("First commit by: ", Console.ANSI_BLUE, false);
        System.out.println(Git.getCaseFirstCommitAuthor(caseName));

        getTextLineBreak("Testing. Results");
        tmpBugs = checkRelativePathsInForEach();
        printBugs("1. Absolute paths in for-each", tmpBugs);
        bugs.addAll(tmpBugs);

        tmpBugs = checkBr();
        printBugs("2. Br tags without test expression", tmpBugs);
        bugs.addAll(tmpBugs);
//
        tmpBugs = checkIf();
        printBugs("3. Missing paths in test expression", tmpBugs);
        bugs.addAll(tmpBugs);

        getTextLineBreak("Testing. End");


        return bugs;
    }

    private static void printBugs(String testCaseName, List<String> findedBugs) {
        Console.printMessage(testCaseName + ": "
                , Console.ANSI_BLUE);
        if (findedBugs.size() != 0) {
            Console.printMessage("Finded " + findedBugs.size() + " bugs. \n\tInfo: " + findedBugs.toString().replaceAll(",", "\n\t")
                    , Console.ANSI_RED);
        } else {
            Console.printMessage("OK"
                    , Console.ANSI_GREEN);
        }
        System.out.println("\n\n");
    }

    private static void getTextLineBreak(String text) {
        int lineLength = 40;
        int textLength = text.length();
        int oneSideLine = (lineLength - textLength) / 2;
        if (oneSideLine % 2 == 0)
            oneSideLine += 1;
        String tmp = lineBreak.substring(0, oneSideLine - 1);
        System.out.print("\n" + tmp);
        if (!text.isEmpty()) {
            Console.printMessage(" " + text + " ", Console.ANSI_YELLOW, false);
        }
        tmp = lineBreak.substring(0, oneSideLine - 1);
        System.out.println(tmp + "\n");
    }

    private static void getTextLineBreak() {
        getTextLineBreak("");
    }

    private static List<String> checkRelativePathsInForEach() {
        ArrayList<String> findedBugs = new ArrayList<>();
        for (Content content : xslt_root.getDescendants()) {
            if ((content instanceof Element)) {
                Element element = (Element) content;
                if (element.getName().contains("for-each")) {
                    String forEachPath = JDOMFunctions.getNormalLine(element.getAttributeValue("select"));
                    for (Content childContent : element.getDescendants()) {
                        if (childContent instanceof Element) {
                            Element childElement = (Element) childContent;
                            try {
                                String testVal = JDOMFunctions.getNormalLine(childElement.getAttributeValue("test"));

                                if (testVal.contains(forEachPath)
                                        | testVal.contains(forEachPath.substring(forEachPath.indexOf("/", 3)))) {
//                                   Integer elementLineNumber = JDOMFunctions.getLineNumberOfElement(childElement, xslt_text);
//                                   if (elementLineNumber == -1) {
//                                       System.out.println("Error while finding line number");
//                                       JDOMFunctions.getLineNumberOfElement(childElement, xslt_text);
//                                   } else {
//                                       findedBugs.add(elementLineNumber);
//                                   }
                                    findedBugs.add(testVal);
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            }
        }
        return findedBugs;
    }

    private static List<String> checkBr() {
        ArrayList<String> findedBugs = new ArrayList<>();
        for (Content content : xslt_root.getDescendants()) {
            if ((content instanceof Element)) {
                Element element = (Element) content;
                if (element.getName().contains("br")) {
                    Element parent = element.getParentElement();
                    int brPos = parent.getChildren().indexOf(element);
                    if (brPos > 0) {
                        if (parent.getChildren().get(brPos - 1).getName().equals("strong")) {
                            continue;
                        }
                    }
                    if (brPos == (parent.getChildren().size() - 1))
                        continue;
                    if (parent.getName().equals("span")) {
                        continue;
                    }
                    if (!parent.getName().equals("if")) {
                        findedBugs.add("bug");
                        continue;
                    }
                    if (parent.getAttributeValue("test").toLowerCase().contains("диагноз")) {
                        continue;
                    }

                    if (parent.getChildren().size() > 1) {
                        findedBugs.add("bug");
                    }
                }
            }
        }
        return findedBugs;
    }

    private static List<String> checkIf() {
        bugsIf = new ArrayList<>();
        for (Content content : xslt_root.getDescendants()) {
            if ((content instanceof Element)) {
                Element element = (Element) content;
                if (element.getName().contains("if")) {
                    checkIfForChildren(element);
                }
            }
        }
        return bugsIf.stream().distinct().collect(Collectors.toList());
    }

    private static void checkIfForChildren(Element element) {
        List<String> testValues = new ArrayList<>();
        List<String> testValuesExact = new ArrayList<>();

        String elementTestVal = JDOMFunctions.getNormalLine(element.getAttributeValue("test"));
        elementTestVal = elementTestVal.replaceAll("'1' = '0'", "").replaceAll(" or ", " ")
                .replaceAll(" and ", " ");
        elementTestVal = JDOMFunctions.getNormalLine(elementTestVal);
        testValuesExact.addAll(Arrays.asList(elementTestVal.split(" ")));

        for (Element child : element.getChildren()) {
            if (child.getName().equals("if")) {
                String testVal = JDOMFunctions.getNormalLine(child.getAttributeValue("test"));
                if (testVal.contains("string-length")
                        | testVal.contains("$")
                        | testVal.endsWith("units")
                        | testVal.endsWith("units]")
                        | testVal.equals("="))
                    continue;
                if (child.getChildren().size() == 1) {
                    if (child.getChildren().get(0).getName().equals("br"))
                        continue;
                }
                testVal = testVal.replaceAll("'1' = '0'", "").replaceAll(" or ", " ")
                        .replaceAll(" and ", " ");
                testVal = JDOMFunctions.getNormalLine(testVal);
                testValues.addAll(Arrays.asList(testVal.split(" ")).stream()
                        .filter(s -> (!s.contains("units"))
                                & !s.contains("position")
                                & s.length() > 5
                                & !s.contains("true")
                                & !s.contains("magnitude")
                        )
                        .collect(Collectors.toList()));
            }
        }
        testValues = testValues.stream().distinct().collect(Collectors.toList());
        testValuesExact = testValuesExact.stream().distinct().collect(Collectors.toList());


        List<String> unique = new ArrayList<>(testValues);
        if (unique.removeAll(testValuesExact))
            if (unique.size() > 0)
                bugsIf.add(unique.get(0));

    }

    private static String getRootString() {
        Element root_template = JDOMFunctions.findElWithNameAndAttr(xslt_root, "match", "/*:", "template");
        return root_template.getAttribute("match").getValue();
    }
}
