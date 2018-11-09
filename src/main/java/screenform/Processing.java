package screenform;

import files.FilesIO;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class Processing {
    private static List<String> rows;

    public static void processXSLT() throws Exception {
        rows = Files.readAllLines(new File(FilesIO.input).toPath(), Charset.forName("UTF-8"));
        one();
        thirteen();
        two();
        three();
        four();
        five();
        nine();
        ten();
        twelve();
        fourteen();
        twenty();

        tabToWhite();
        FilesIO.writeToFile(rows);
    }

    static String deleteAllNonCharacter(String str) {
        return str.replace(":", "").replace(".", "");
    }

    private static void one() {
        boolean deleting = false;
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).contains("<table")) {
                deleting = true;
            }
            if (rows.get(i).contains("</table>")) {
                rows.remove(i);
                return;
            }
            if (deleting) {
                rows.remove(i--);
            }
        }
    }

    private static void two() {
        String styleName = "23456789123456789123456789";
        String styles = "    <style>\n" +
                "     @import url('https://fonts.googleapis.com/css?family=Open+Sans');\n" +
                "     .xslt_85s{\n" +
                "         margin: 0pt;\n" +
                "         padding-left: 0;" +
                "     }\n" +
                "     .xslt_85s .part{\n" +
                "         line-height: 24px;\n" +
                "         font-family: Open Sans;\n" +
                "         font-weight: bold;\n" +
                "         font-size: 15px;\n" +
                "         color: #333333;\n" +
                "     }\n" +
                "     .xslt_85s .mytd{\n" +
                "         line-height: 24px;\n" +
                "         font-family: Open Sans;\n" +
                "         font-size: 15px;\n" +
                "         color: #333333;\n" +
                "         padding-bottom: 24px;" +
                "     }\n" +
                "     .xslt_85s .myml{\n" +
                "         padding-top: 24px;\n" +
                "     }\n" +
                "     .xslt_85s .myth{\n" +
                "         text-align: left;\n" +
                "         line-height: 24px;\n" +
                "         font-family: Open Sans;\n" +
                "         font-size: 15px;\n" +
                "         color: #757575;\n" +
                "         font-weight: normal;\n" +
                "         padding-top: 24px\n" +
                "     }\n" +
                "     .xslt_85s .lefttd{\n" +
                "         line-height: 24px;\n" +
                "         font-family: Open Sans;\n" +
                "         font-size: 15px;\n" +
                "         color: #333333;\n" +
                "     }\n" +
                "     .xslt_85s strong{\n" +
                "         line-height: 24px;\n" +
                "         font-family: Open Sans;\n" +
                "         font-size: 15px;\n" +
                "         font-weight: normal;\n" +
                "         color: #333333;\n" +
                "     }\n" +
                "     .xslt_85s span{\n" +
                "         line-height: 24px;\n" +
                "         font-family: Open Sans;\n" +
                "         font-size: 15px;\n" +
                "         color: #333333;\n" +
                "     }" +
                "     .xslt_85s td{\n" +
                "         border-spacing: 0;\n" +
                "         padding: 0;\n" +
                "     }\n" +
                "     .xslt_85s th{\n" +
                "     \t\t\t padding-left: 0;\n" +
                "     }\n" +
                "     .xslt_85s table{\n" +
                "     \t\t\t\tborder-spacing: 0;\n" +
                "     }</style>";
        List<String> newStyleDef = Arrays.asList(styles.split("\\n"));

        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("<style>")) {
                styleName = rows.get(i + 1).replaceAll("[^A-Za-z0-9_]+", "");
                //changing definition style
                String styleNameDef = "23456789123456789123456789";
                for (int j = 0; j < newStyleDef.size(); j++) {
                    String nowDef = newStyleDef.get(j);
                    if (nowDef.contains("<style>")) {
                        styleNameDef = newStyleDef.get(j + 2).replaceAll("[^A-Za-z0-9_]+", "");
                        newStyleDef.set(j, newStyleDef.get(j).replace(styleNameDef, styleName));
                    } else if (nowDef.contains(styleNameDef)) {
                        newStyleDef.set(j, newStyleDef.get(j).replace(styleNameDef, styleName));
                    }
                }
                String nowChangeDef = now;
                while (!nowChangeDef.contains("</style>")) {
                    nowChangeDef = rows.get(i);
                    rows.remove(i);
                }
                int off = 0;
                for (String el : newStyleDef) {
                    rows.add(i + off++, el);
                }
            } else if (now.contains(styleName)) {
                rows.set(i, rows.get(i).replace(styleName, styleName + "s"));
            }
        }
    }

    private static void three() {
        boolean notFirst = false;
        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("<table")) {
                if (!notFirst) {
                    rows.set(i, "<table class=\"mytd\" width=\"648px\" align=\"center\">");
                    if ((rows.get(i + 1).contains(">")) & (!rows.get(i + 1).contains("<"))) rows.remove(i + 1);
                    notFirst = true;
                } else {
                    rows.set(i, "<table>");
                    if ((rows.get(i + 1).contains(">")) & (!rows.get(i + 1).contains("<"))) rows.remove(i + 1);
                }
            }
        }
    }

    private static void four() {
        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("width=\"116pt\"")) {
                rows.set(i, rows.get(i).replace("width=\"116pt\"", ""));
            }
            if (now.contains("style=\" border-top: 0.5pt solid rgba(0,0,0,0.4);\">")) {
                rows.remove(i--);
            }
        }
    }

    private static void five() {
        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("<tr>")) {
                if (rows.get(i + 1).contains("colspan")) {
                    String nowChange = now;
                    while (!nowChange.contains("</tr>")) {
                        nowChange = rows.get(i);
                        rows.remove(i);
                    }
                }
            }
        }
        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("</td>")) {
                if (now.contains("<td")) {
                    rows.remove(i--);
                }
            }
        }


        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("</td>")) {
                if (rows.get(i + 1).contains("<td")) {
                    rows.remove(i);
                    rows.remove(i);
                }
            }
        }
    }

    private static void nine() {
        boolean obsOsmFound = false;
        boolean bodyFound = false;
        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("test=\"*:Общий_осмотр != ''")) {
                obsOsmFound = true;
            }
            if (now.contains("<tbody>")) {
                bodyFound = true;
            }
            if (now.contains("</tbody>")) {
                bodyFound = false;
            }
            if (obsOsmFound & bodyFound) {
                if (now.contains("<tr>") & rows.get(i + 1).contains("<td class=\"lefttd\">")) {
                    rows.remove(i);
                    rows.remove(i);
                    int off = 0;
                    String bufForFindCloseTag = rows.get(i);
                    while (!bufForFindCloseTag.contains("</tr>")) {
                        bufForFindCloseTag = rows.get(i + off++);
                    }
                    rows.remove(i + off - 2);
                    rows.remove(i + off - 2);
                }
            }
        }
    }

    private static void ten() {
        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("span class=\"part\"")) {
                if (now.contains("<br/>")) {
                    rows.set(i, rows.get(i).replace("<br/>", " "));
                }
            }
        }
    }

    private static void twelve() {
        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("<strong>")) {
                if (rows.get(i - 1).contains("<br/>")) {
                    rows.remove(i - 1);
                }
            }
        }
    }

    private static void thirteen() {
        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("<td")) {
                if (!now.contains(">")) {
                    while (!now.contains(">")) {
                        rows.remove(i);
                        now = rows.get(i);
                    }

                    rows.set(i, now.substring(now.indexOf(">") + 1));
                    if (!now.contains("/>")) {
                        rows.add(i, "<td>");
                    }
                }
            }
        }
    }

    private static void fourteen() {
        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains("<br/>.")) {
                rows.set(i, now.replaceFirst("<br/>.", "<br/>"));
            }
        }
    }

    private static void twenty() {
        for (int i = 0; i < rows.size(); i++) {
            String now = rows.get(i);
            if (now.contains(">рост")) {
                rows.set(i, now.replaceFirst("рост", "Рост"));
            }
            if (now.contains(">вес")) {
                rows.set(i, now.replaceFirst("вес", "Вес"));
            }
            if (now.contains("предположительная дата явки")) {
                rows.set(i, now.replaceFirst("предположительная", "Предположительная"));
            }
        }
    }

    private static void tabToWhite() {
        for (int i = 0; i < rows.size(); i++) {
            rows.set(i, rows.get(i).replaceAll("\\t", " "));
        }
    }
}
