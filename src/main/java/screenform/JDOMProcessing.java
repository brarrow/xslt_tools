package screenform;

import console.Console;
import files.FilesIO;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import webstand.cases.CasesFunctions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class JDOMProcessing {
    private static String in = FilesIO.input;
    private static String out = FilesIO.out.toString();
    private static Namespace xsl = Namespace.getNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");

    public static void processXSLT() {
        in = FilesIO.input;
        out = FilesIO.out.toString();

        addMD5Comment(out);
        one(out);           //Remove table in div with name talon
        two(out);           //Make weight, height, date in recommendation from cap char
        three(out);         //Remove not needed td elements
        four(out);          //Add new style section
        five(out);          //Delete all attributed in tables exclude main table
        six(out);           //Move left headers to main table
        seven(out);         //Remove tr/td container in left column attributes and add costili to it
        eight(out);         //Make bold in diagnosis
        nine(out);          //Make relative paths from direct
        ten(out);           //Move obsh diag and soput to begin
        eleven(out);        //All main headers in <tr> <td class=myml>
        //
        twelve(out);        //Move parameters from left table to main table
        thirteen(out);      //Next visit recommendations to good and nice condition
        fourteen(out);      //All main headers from capital trim
        fifteen(out);       //To get good and nice Arterial pressure
        sixteen(out);       //To get good and nice local status
        seventeen(out);     //Researches from capital char
        eighteen(out);      //Wrap header of general inspection in tr/td
        nineteen(out);      //Change Complication and Concomitant disease headers from part to myth

        twenty(out);        //Replace whitespaces in "In Period" value
        twentyone(out);     //Delete br in Interpretation
        twentytwo(out);     //Make in the moment compl from low case
        twentythree(out);   //Remove br from at the next reception
        twentyfour(out);    //Delete extra whitespace before edizm
        twentyfive(out);    //Make gaping before EVALUATION headers
        twentyseven(out);   //Remove <br/> in anamnesis

        twentysix(out, "*:Принимает_препарат");
        twentyfive(out);

        twentysix(out, "*:Жалобы/");
        twentyfive(out);

        twentysix(out, "*:Жалобы_и_анамнез_заболевания/*:Тесты");
        twentyfive(out);

        twentysix(out, "*:Подробности_истории_болезни");
        twentyfive(out);

        twentysix(out, "*:Местный_статус");
        twentyfive(out);

        twentysix(out, "*:score/*:Балл/");
        twentyfive(out);

        if (out.toLowerCase().contains("gynecologist")) {
            twentysix(out, "*:Гинекологический_осмотр");
            twentyfive(out);    //Make gaping
            twentysix(out, "*:Кольпоскопия/*:Кольпоскопия/*:data/*:Любое_событие_as_Point_Event/*:data/*:Подробности/*:Шейка_матки");
            twentyfive(out);    //Make gaping
            twentysix(out, "*:Жалобы_и_анамнез_заболевания/*:Менструальный_цикл");
            twentyfive(out);    //Make gaping
        }
        if (out.toLowerCase().contains("otolaryngologist")) {
            twentysix(out, "/*:Осмотр_оториноларинголога/*:Осмотр_носа_и_носоглотки/*:Общий_осмотр/*:Общий_осмотр/*:data/*:Любое_событие_as_Point_Event/*:data/*:Осмотр_носа/*:Форма/*:value/*:value");
            twentyfive(out);    //Make gaping
            twentysix(out, "*:Осмотр_ротоглотки__openBrkt_фарингоскопия_closeBrkt_");
            twentyfive(out);    //Make gaping
            twentysix(out, "*:Отоневрологический_осмотр");
            twentyfive(out);    //Make gaping

        }

        if (out.toLowerCase().contains("ophthalmologist")) {
            twentysix(out, "*:Инструментальные_исследования_глаз");
            twentyfive(out);    //Make gaping
            twentysix(out, "*:Острота_зрения");
            twentyfive(out);    //Make gaping
            twentysix(out, "*:Рефрактометрия");
            twentyfive(out);    //Make gaping

        }
        //twentyeight(out);
        deleteCostiliLeft(out); // delete costili from left attributes
    }

    private static org.jdom2.Document useSAXParser(String fileName) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();

            return saxBuilder.build(new File(fileName));
        } catch (Exception ignored) {
            Console.printMessage("Error: while parsing xslt document.", Console.ANSI_RED);
        }
        return null;
    }

    //add generation information
    private static void addMD5Comment(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();

        try (InputStream is = Files.newInputStream(Paths.get(FilesIO.input))) {
            String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
            root.addContent(0, new Comment("md5:" + md5));
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            root.addContent(0, new Comment("generated:" + dtf.format(now)));
            saveXSLT(doc, filePath);

        } catch (Exception ignored) {
            Console.printMessage("Failed: add generation information.", Console.ANSI_YELLOW);
        }
    }

    //remove table in div with name talon
    private static void one(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element div = findElWithNameAndAttr(root, "class", "xslt", "div");
            Element table = div.getChildren().get(0);
            if (table.getName().equals("table")) {
                table.detach();
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: remove table in div with name talon.", Console.ANSI_YELLOW);
        }

    }

    //Make weight, height, date in recommendation from cap char
    private static void two(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element weight = new Element("buf");
            Element height = new Element("buf");
            Element date = new Element("buf");
            for (Content content : root.getDescendants()) {
                if (content instanceof Element) {
                    if (((Element) content).getName().equals("strong")
                            | ((Element) content).getName().equals("span")) {
                        String text = ((Element) content).getText();
                        if (text.startsWith("рост")) {
                            height = (Element) content;
                        }
                        if (text.startsWith("вес")) {
                            weight = (Element) content;
                        }
                        if (text.startsWith("предположительная дата явки")) {
                            date = (Element) content;
                        }
                    }
                }
            }
            height.setText(height.getText().replaceFirst("рост", "Рост"));
            weight.setText(weight.getText().replaceFirst("вес", "Вес"));
            date.setText(date.getText().replaceFirst("предпол", "Предпол"));
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: make weight, height, date in recommendation from capital char.", Console.ANSI_YELLOW);
        }
    }

    //remove not needed td elements
    private static void three(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();

        List<String> safePatterns = new ArrayList<>();
        safePatterns.add("border-top: 0.5pt solid rgba(0,0,0,0.4); height:1pt padding-top: 6pt;" +
                "padding-right: 0pt;" +
                "padding-bottom: 0pt;");

        try {
            for (Content content : root.getDescendants()) {
                if (content instanceof Element) {
                    if (((Element) content).getName().equals("td")) {
                        if (((Element) content).hasAttributes()) {
                            List<Attribute> attributes = ((Element) content).getAttributes();
                            boolean forRemove = false;
                            for (Attribute attribute : attributes) {
                                if (attribute.getName().equals("colspan")
                                        | attribute.getName().equals("valign")) {
                                    forRemove = true;
                                }
                                if (safePatterns.stream().anyMatch(el -> attribute.getValue().contains(el))) {
                                    forRemove = false;
                                    break;
                                }
                            }
                            if (forRemove) {
                                while (((Element) content).hasAttributes()) {
                                    ((Element) content).getAttributes().get(0).detach();
                                }
                            }
                        }
                    }
                }
            }
            deleteEmptyTd(root);
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: remove not needed td elements.", Console.ANSI_YELLOW);
        }
    }

    //add new style section
    private static void four(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element styleEl = null;
            for (Content content : root.getDescendants()) {
                if (content instanceof Element) {
                    if (((Element) content).getName().equals("style")) {
                        styleEl = (Element) content;
                        break;
                    }
                }
            }
            String stN = CasesFunctions.findCaseWithPath(in).replace("test", "xslt") + "s";
            String styles = "\n" +
                    "     ." + stN + "{\n" +
                    "         margin: 0pt;\n" +
                    "         padding-left: 0;\n" +
                    "     }\n" +
                    "     ." + stN + " .part{\n" +
                    "         line-height: 24px;\n" +
                    "         font-family: Open Sans;\n" +
                    "         font-weight: bold;\n" +
                    "         font-size: 15px;\n" +
                    "         color: #333333;\n" +
                    "     }\n" +
                    "     ." + stN + " .mytd{\n" +
                    "         line-height: 24px;\n" +
                    "         font-family: Open Sans;\n" +
                    "         font-size: 15px;\n" +
                    "         color: #333333;\n" +
                    "         padding-bottom: 20px;\n" +
                    "     }\n" +
                    "     ." + stN + " .myml{\n" +
                    "         padding-top: 20px;\n" +
                    "     }\n" +
                    "     ." + stN + " .myth{\n" +
                    "         text-align: left;\n" +
                    "         line-height: 24px;\n" +
                    "         font-family: Open Sans;\n" +
                    "         font-size: 15px;\n" +
                    "         color: #757575;\n" +
                    "         font-weight: normal;\n" +
                    "         padding-top: 20px;\n" +
                    "     }\n" +
                    "     ." + stN + " .lefttd{\n" +
                    "         line-height: 24px;\n" +
                    "         font-family: Open Sans;\n" +
                    "         font-size: 15px;\n" +
                    "         color: #333333;\n" +
                    "     }\n" +
                    "     ." + stN + " strong{\n" +
                    "         line-height: 24px;\n" +
                    "         font-family: Open Sans;\n" +
                    "         font-size: 15px;\n" +
                    "         font-weight: normal;\n" +
                    "         color: #333333;\n" +
                    "     }\n" +
                    "     ." + stN + " span{\n" +
                    "         line-height: 24px;\n" +
                    "         font-family: Open Sans;\n" +
                    "         font-size: 15px;\n" +
                    "         color: #333333;\n" +
                    "     }\n" +
                    "     ." + stN + " td{\n" +
                    "         border-spacing: 0;\n" +
                    "         padding: 0;\n" +
                    "     }\n" +
                    "     ." + stN + " th{\n" +
                    "         padding: 0;\n" +
                    "     }\n" +
                    "     ." + stN + " table{\n" +
                    "         border-spacing: 0;\n" +
                    "     }";
            styleEl.setText(styles);
            for (Content content : root.getDescendants()) {
                if (content instanceof Element) {
                    try {
                        if (((Element) content).getAttributeValue("class").equals(stN.substring(0, stN.length() - 1))) {
                            ((Element) content).setAttribute("class", stN);
                            break;
                        }
                        break;
                    } catch (Exception ignored) {
                    }
                }
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: add new style section.", Console.ANSI_YELLOW);
        }

    }

    //delete all attributed in tables exclude main table
    private static void five(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element div = findElWithNameAndAttr(root, "class", "xslt", "div");
            Element table = div.getChildren().get(0);
            if (table.getName().equals("table")) {
                List<Attribute> attributes = new ArrayList<>();
                attributes.add(new Attribute("class", "mytd"));
                attributes.add(new Attribute("width", "648px"));
                attributes.add(new Attribute("align", "center"));
                table.setAttributes(attributes);
            }
            for (Content content : root.getDescendants()) {
                if (content instanceof Element) {
                    if (((Element) content).getName().equals("table")) {
                        String width = ((Element) content).getAttributeValue("width");
                        if (width != null) {
                            if (!width.equals("648px")) {
                                ((Element) content).setAttributes(null);
                            }
                        }
                    }
                }
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: delete all attributed in tables exclude main table.", Console.ANSI_YELLOW);
        }
    }

    //move left headers to main table
    private static void six(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            List<Element> forDelete = new ArrayList<>();
            for (Content content : root.getDescendants()) {
                if (content instanceof Element) {
                    if (((Element) content).getName().equals("td")) {
                        Element parent = content.getParentElement();
                        int posTd = parent.getChildren().indexOf(content);
                        try {
                            Element next = parent.getChildren().get(posTd + 1);
                            if (next.getName().equals("td")) {
                                forDelete.add((Element) content);
                                forDelete.add(next);
                            }
                        } catch (Exception ignored) {
                        }

                    }
                }
            }
            while (forDelete.size() > 0) {
                Element first = forDelete.remove(0);
                Element second = forDelete.remove(0);
                second.detach();
                first.addContent(second.removeContent());
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: move left headers to main table.", Console.ANSI_YELLOW);
        }
    }

    //remove tr/td container in left column attributes and add costili to it
    private static void seven(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element leftTd = findElWithNameAndAttr(root, "class", "lefttd", "td");
            while (leftTd != null) {
                List<Content> contentList = leftTd.removeContent();
                for (Content content : contentList) {
                    if (content instanceof Element) {
                        if (((Element) content).getName().equals("strong")) {
                            ((Element) content).setText("left" + ((Element) content).getText());
                        }
                    }
                }
                Element tr = leftTd.getParentElement();
                Element parent = tr.getParentElement();
                int trInd = parent.getContent().indexOf(tr);
                parent.getContent().remove(trInd);
                parent.addContent(trInd, contentList);
                leftTd = findElWithNameAndAttr(root, "class", "lefttd", "td");
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: remove tr/td container in left column attributes and add costili to it.", Console.ANSI_YELLOW);
        }
    }

    //make bold in diagnosis
    private static void eight(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element element = findElWithNameAndAttr(root, "name", "strong", "element");
            while (element != null) {
                element.setName("strong");
                element.setNamespace(null);
                List<Attribute> attributeList = new ArrayList<>();
                attributeList.add(new Attribute("class", "part"));
                element.setAttributes(attributeList);
                element = findElWithNameAndAttr(root, "name", "strong", "element");
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: make bold in diagnosis.", Console.ANSI_YELLOW);
        }
    }

    //make relative paths from direct
    private static void nine(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element tmpl = findElWithNameAndAttr(root, "match", "*:", "template");

            if (tmpl == null) {
                Console.printMessage("Error: can't get template name!", Console.ANSI_RED);
                return;
            }

            String nameToDel = tmpl.getAttributeValue("match") + "/";
            if (!nameToDel.startsWith("/")) {
                nameToDel = "/" + nameToDel;
            }

            for (Content content : root.getDescendants()) {
                if (content instanceof Element) {
                    List<String> atr_names = new ArrayList<>(List.of("test", "select"));
                    for (String atr : atr_names) {
                        try {
                            String atr_value = ((Element) content).getAttributeValue(atr);
                            if (atr_value == null) continue;

                            String trash = "'1' = '0'";
                            try {
                                while (atr_value.contains(trash)) {
                                    int posFrom = atr_value.indexOf(trash);
                                    int posTo = posFrom + atr_value.substring(posFrom).indexOf(" or ") + " or ".length();
                                    atr_value = atr_value.replace(atr_value.substring(posFrom, posTo), "");
                                }
                            } catch (Exception ignored) {
                                ignored.printStackTrace();
                            }
                            ((Element) content).setAttribute(atr, atr_value);

                            Element parent = content.getParentElement();
                            int depth = 8;
                            boolean forEachInParent = false;
                            while (depth > 0 & !forEachInParent) {
                                if (parent.getName().contains("for-each")) {
                                    forEachInParent = true;
                                }
                                parent = parent.getParentElement();
                                depth--;
                            }
                            if (forEachInParent) continue;

                            atr_value = atr_value.replace(nameToDel, "").trim();
                            ((Element) content).setAttribute(atr, atr_value);

                            //for good and nice Interpr results
                            if (((Element) content).getName().contains("if")) {
                                if (content.getParentElement().getName().contains("for-each")) {
                                    String test_atr = ((Element) content).getAttributeValue("test");
                                    if (test_atr.contains("*:Интерпретация_результатов_обследования/*:Исследование/*:Интерпретация_результатов/*:data/")) {
                                        ((Element) content).setAttribute("test", test_atr
                                                .replace("*:Интерпретация_результатов_обследования/*:Исследование/", ""));
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: make relative paths from direct", Console.ANSI_YELLOW);
        }
    }

    //Move obsh diag and soput to begin
    private static void ten(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        forEachTd(root);
        deleteEmptyTr(root);
        try {
            Element sopDiag;
            try {
                sopDiag = findElWithNameAndCont(root, "Сопутствующий диагноз", "if");
                Element spanSopDiag = findElWithNameAndCont(sopDiag, "Сопутствующий диагноз", "span");
                spanSopDiag.setAttribute("class", "myth");
                spanSopDiag.setText("Сопутствующее заболевание");
            } catch (Exception ex) {
                sopDiag = findElWithNameAndCont(root, "Сопутствующее заболевание", "if");
                Element spanSopDiag = findElWithNameAndCont(sopDiag, "Сопутствующее заболевание", "span");
                spanSopDiag.setAttribute("class", "myth");
            }
            Comment sopDiagComment = null;
            Element sopDiagDest = findElWithNameAndCont(root, "Сопутствующее заболевание", "tbody");

            int sopDiagIndex = sopDiag.getParent().getContent().indexOf(sopDiag);
            if (sopDiag.getParent().getContent().get(sopDiagIndex - 2) instanceof Comment) {
                sopDiagComment = (Comment) sopDiag.getParent().getContent().get(sopDiagIndex - 2);
            }

            sopDiagDest.addContent(0, sopDiag.detach());
            if (sopDiagComment != null) {
                sopDiagDest.addContent(0, new Text("\n"));
                sopDiagDest.addContent(0, sopDiagComment.detach());
                sopDiagDest.addContent(0, new Text("\n"));
            }


            Element osnDiag = findElWithNameAndCont(root, "Основной диагноз", "if");
            Element spanOsnDiag = findElWithNameAndCont(osnDiag, "Основной диагноз", "span");
            spanOsnDiag.setAttribute("class", "myth");
            Comment osnDiagComment = null;
            Element osnDiagDest = findElWithNameAndCont(root, "Основной диагноз", "tbody");
            try {
                List<Element> osnDiagEls = osnDiag.getChild("tr").getChild("td").getChildren("if", xsl);
                for (Element element : osnDiagEls) {
                    if (element.getAttributeValue("test").contains("Диагностический_статус")) {
                        element.getChild("strong").setAttribute("class", "part");
                    }
                }
            } catch (Exception ignored) {
            }

            int osnDiagIndex = osnDiag.getParent().getContent().indexOf(osnDiag);
            try {
                if (osnDiag.getParent().getContent().get(osnDiagIndex - 2) instanceof Comment) {
                    osnDiagComment = (Comment) osnDiag.getParent().getContent().get(osnDiagIndex - 2);
                }
            } catch (Exception ignored) {
            }
            osnDiagDest.addContent(0, osnDiag.detach());
            if (osnDiagComment != null) {
                osnDiagDest.addContent(0, new Text("\n"));
                osnDiagDest.addContent(0, osnDiagComment.detach());
                osnDiagDest.addContent(0, new Text("\n"));

            }
            saveXSLT(doc, filePath);
        } catch (Exception e) {
            Console.printMessage("Failed: moving diagnosis to begin.", Console.ANSI_YELLOW);
        }

    }

    //all main headers in <tr> <td class=myml>
    private static void eleven(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            forEachTd(root);
            deleteEmptyTr(root);

            List<Element> listElement = root.getChildren().get(1).getChild("html").getChild("body").getChild("div").getChildren().get(0).getChildren().get(0).getChildren();//.get(1).getChildren().get(0).getChildren();//.get(2).getChild("tbody").getChildren();
            Element obsOsm = null;
            for (Element el : listElement) {
                if (obsOsm != null) break;
                List<Attribute> buf = el.getAttributes();
                for (Attribute atr : buf) {
                    if (atr.getValue().contains("*:Общий_осмотр")) obsOsm = el;
                }
            }

            Element ob;
            if (filePath.contains("2395558")) {
                ob = (Element) obsOsm.getChild("tr").getChild("td").getContent(3);
            } else {
                if (filePath.contains("22954")) {
                    ob = findElWithNameAndAttr(root, "test", "*:Жалобы_и_анамнез_заболевания//*:Жалобы_и_анамнез_заболевания//*:Подробности_истории_болезни", "if");
                } else {
                    ob = obsOsm.getChild("tr");
                }
            }

            if (!filePath.contains("22954")) { //esli est' obs osm
                if (!ob.getName().equals("variable")) {
                    ob = ob.getChild("td");
                }
                List<Content> temp = ob.removeContent();
                ob.setContent(new Element("table").setAttribute("align", "left").setContent(new Element("tbody").setContent(temp)));
            }

            forEachStrong(ob);
            forEachStrong(ob);

            saveXSLT(doc, filePath);
        } catch (Exception e) {
            Console.printMessage("Failed: make all subtitles from another line.", Console.ANSI_YELLOW);
        }
    }

    //Move parameters from left table to main table
    private static void twelve(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            deleteEmptyTr(root);

            Element obsOsm = findElWithNameAndCont(root, "Общий осмотр", "span");
            if (obsOsm == null) {
                return;
            }
            while (!obsOsm.getName().equals("table")) {
                obsOsm = obsOsm.getParentElement();
            }
            if (obsOsm.getChild("tbody") != null) {
                List<Content> tbodyContent = obsOsm.getChild("tbody").removeContent();
                obsOsm.removeChild("tbody");
                obsOsm.addContent(tbodyContent);

            }

            for (Element el : obsOsm.getChildren()) {
                if (el.getName().contains("if")) {
                    if ((el).getChild("tr") != null) {

                        List<Content> trContent = (el).getChild("tr").removeContent();
                        el.getChild("tr").detach();
                        el.setContent(trContent);
                    }
                    if ((el).getChild("td") != null) {
                        List<Content> tdContent = (el).getChild("td").removeContent();
                        el.getChild("td").detach();
                        el.setContent(tdContent);
                    }
                    if (el.getChildren().size() == 1 && el.getAttributeValue("test").contains("count"))
                        el.getChildren().get(0).addContent((new Element("span")).setText(". "));
                    else
                        el.addContent((new Element("span")).setText(". "));
                }
            }
            Element thead = obsOsm.getChild("thead").detach();


            List<Content> contentToMove = obsOsm.removeContent();
            if (thead != null) {
                obsOsm.addContent(thead);
            }


            if (obsOsm.getName().equals("tbody")) {
                obsOsm.detach();
            }

            Element obsSost = findElWithNameAndCont(root, "Общее", "strong");

            if (obsSost == null) {
                obsSost = findElWithNameAndCont(root, "Состояние беременности", "strong");
            }
//            Element var_el = new Element("variable", xsl);
//            var_el.setAttribute("name", "left_attr");
//            var_el.setContent(contentToMove);
//
//            Element with_param_el = new Element("with-param", xsl);
//            with_param_el.setAttribute("name", "string");
//            with_param_el.setAttribute("select", "$left_attr");
//
//
//            Element call_tmpl_el = new Element("value-of", xsl);
//            call_tmpl_el.setAttribute("select", "$left_attr");
//
//            Element attr_el = new Element("attribute", xsl);
//            attr_el.addContent("myml");
//            attr_el.setAttribute("name", "class");
//
//            Element if_el = new Element("if", xsl);
//            if_el.setAttribute("test", "$left_attr != \'\'");
//            if_el.addContent(attr_el);


            Element td_el = new Element("td");
//            td_el.addContent(var_el);
//            td_el.addContent(if_el);
//            td_el.addContent(call_tmpl_el);
            td_el.addContent(contentToMove);

            Element tr_el = new Element("tr");
            tr_el.addContent(td_el);

            while (!obsSost.getName().equals("if")) {
                obsSost = obsSost.getParentElement();
            }
            Element obsSostParent = obsSost.getParentElement();
            int obsSostPos = obsSostParent.getContent().indexOf(obsSost);
            try {
                Element nextEl = obsSostParent.getChildren().get(obsSostParent.getChildren().indexOf(obsSost) + 1);
                if (nextEl.getAttributeValue("test").contains("Соотношение_между")) {
                    Element variable = findElWithNameAndAttr(obsSost, "name", "_", "variable");
                    variable.addContent(nextEl.detach());
                }
            } catch (Exception ignored) {
            }

            obsSostParent.addContent(obsSostPos + 1, tr_el);

            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            Console.printMessage("Failed: move parameters from left table to main table.", Console.ANSI_YELLOW);
        }
    }

    //Next visit recommendations to good and nice condition
    private static void thirteen(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            deleteEmptyTr(root);
            Element recomNextStrongEl;
            Element recomNextEl;
            Content recomNextText = findElWithNameAndCont(root, "Повторный курс/явка", "Text");
            if (recomNextText != null) {
                recomNextStrongEl = (new Element("strong"));
                recomNextStrongEl.setText("Повторный курс/явка");
                recomNextEl = recomNextText.getParentElement();
                recomNextEl.addContent(0, recomNextStrongEl);
            } else {

                Element recomNextSpanEl = findElWithNameAndCont(root, "Рекомендации по последующему приему", "span");
                if (recomNextSpanEl != null) {
                    recomNextSpanEl.setName("strong");
                }
                recomNextStrongEl = findElWithNameAndCont(root, "Рекомендации по последующему приему", "strong");
                recomNextEl = recomNextStrongEl.getParentElement();
            }
            int recomNextStrongPos = recomNextEl.getChildren().indexOf(recomNextStrongEl);

            if ((recomNextStrongPos > 0)) {
                if (recomNextEl.getChildren().get(recomNextStrongPos - 1).getName().contains("br")) {
                    recomNextEl.getChildren().get(recomNextStrongPos - 1).detach();
                }
            }
            recomNextEl.getChildren().add(recomNextStrongPos + 1, new Element("br"));
            recomNextStrongEl.setText(Processing.deleteAllNonCharacter(recomNextStrongEl.getValue()));
            recomNextStrongEl.setAttribute("class", "myth");

            List<Content> toFindDot = recomNextStrongEl.getParentElement().getContent();
            for (int i = 0; i < toFindDot.size(); i++) {
                if (toFindDot.get(i).equals(recomNextStrongEl)) {
                    if (toFindDot.get(i + 1) instanceof Text) {
                        ((Text) toFindDot.get(i + 1)).setText(Processing.deleteAllNonCharacter(((Text) toFindDot.get(i + 1)).getText()));
                    }
                }
            }

            Element forEachEl = new Element("for-each");
            forEachEl.setNamespace(xsl);
            forEachEl.setAttribute("select", "*:Рекомендации/*:Рекомендации_по_последующему_приему");
            Element ifEl = new Element("if");
            ifEl.setNamespace(xsl);
            ifEl.setAttribute("test", "count(preceding-sibling::*) > 1");
            Element attrEl = new Element("attribute");
            attrEl.setNamespace(xsl);
            attrEl.setAttribute("name", "class");
            attrEl.setText("myml");
            ifEl.setContent(attrEl);
            forEachEl.setContent(ifEl);
            recomNextEl.getChildren().add(0, forEachEl);


            Element recomNextParant = recomNextEl.getParentElement();
            int recomNextPos = recomNextParant.getChildren().indexOf(recomNextEl);
            Element trEl = new Element("tr");
            Element tdEl = new Element("td");
            tdEl.setAttribute("padding-bottom", "32px");
            tdEl = tdEl.setAttribute("class", "myml");
            tdEl.setContent(recomNextEl.detach());
            trEl.setContent(tdEl);
            recomNextParant.getChildren().add(recomNextPos, trEl);
            int recomNextPosCont = recomNextParant.getContent().indexOf(trEl);
            if (recomNextParant.getContent().get(recomNextPosCont - 1) instanceof Text) {
                Content dot = recomNextParant.getContent().get(recomNextPosCont - 1).detach();
                tdEl.addContent(dot);
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: make good and nice Recommendations.", Console.ANSI_YELLOW);
        }

    }

    //all main headers from capital trim
    private static void fourteen(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            for (Content el : root.getDescendants()) {
                if (el instanceof Element) {
                    if (((Element) el).getName().equals("call-template")) {
                        if (((Element) el).getAttributeValue("name").contains("string-ltrim")) {
                            Element withParam = ((Element) el).getChildren().get(0);
                            if (withParam != null) {
                                if (withParam.getName().equals("with-param")) {
                                    String name = withParam.getAttributeValue("select").replace("$content", "")
                                            .replace("Up", "").replace("con", "").replace("$v", "").replaceAll("[0-9]", "");
                                    int urovenVloz = 1;
                                    boolean needToChange = false;
                                    if (el.getParentElement().getName().equals("if")) {
                                        try {
                                            if (el.getParentElement().getAttributeValue("test").contains("position")) {
                                                Element ifEl = el.getParentElement();
                                                for (Content content : ifEl.getParentElement().getParentElement().getContent()) {
                                                    if (content instanceof Element) {
                                                        if (((Element) content).getName().equals("span")
                                                                & (((Element) content).getText().contains(":")
                                                                | ((Element) content).getText().contains(","))) {
                                                            throw new Exception("bad");
                                                        }
                                                        if (((Element) content).getName().equals("if")) {
                                                            Element ifChild = ((Element) content).getChildren().get(0);
                                                            if (ifChild.getName().equals("span") & ifChild.getText().contains(",")) {
                                                                throw new Exception("bad");
                                                            }
                                                        }
                                                    }
                                                    if (content instanceof Text) {
                                                        if (content.getValue().contains(",")
                                                                | content.getValue().contains(":")) {
                                                            throw new Exception("bad");
                                                        }
                                                    }
                                                }
                                                try {
                                                    if (ifEl.getParentElement().getName().contains("for-each")) {
                                                        Element mainIf = ifEl.getParentElement().getParentElement();
                                                        for (Element element : mainIf.getChildren()) {
                                                            if (element.getName().equals("strong")) {
                                                                if (element.getAttributeValue("class").contains("myth")) {
                                                                    needToChange = true;
                                                                }
                                                                break;
                                                            }
                                                        }

                                                        if (!needToChange) {
                                                            mainIf = ifEl.getParentElement().getParentElement().getParentElement();
                                                            for (Element element : mainIf.getChildren()) {
                                                                if (element.getName().equals("strong")) {
                                                                    if (element.getAttributeValue("class").contains("myth")) {
                                                                        needToChange = true;
                                                                    }
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                } catch (Exception ignored) {
                                                    continue;
                                                }
                                            }
                                        } catch (Exception ex) {
                                            if (ex.getMessage().equals("bad")) {
                                                continue;
                                            }
                                        }
                                    }
                                    if (name.length() <= urovenVloz | needToChange) {
                                        ((Element) el).setAttribute("name", ((Element) el).getAttributeValue("name").replace("string-ltrim", "string-capltrim"));
                                        //el.detach();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: make all subtitles from capital char.", Console.ANSI_YELLOW);
        }
    }

    //to get good and nice Arterial pressure
    private static void fifteen(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element artDavl = findElWithNameAndAttr(root, "test",
                    "*:Общий_осмотр/*:Артериальное_давление/*:data/*:Любое_событие_as_Point_Event/*:data/*:Комментарий/*:value/rm:value != ''", "if");
            if (artDavl == null) {
                return;
            }
            artDavl = artDavl.getChild("tr").getChild("td");
            artDavl = findElWithNameAndAttr(artDavl, "select", "*:Общий_осмотр/*:Артериальное_давление", "for-each");
            Element forDelete = artDavl.getChildren().get(0).getChildren().get(0);
            if (!forDelete.getAttributeValue("test").toLowerCase().contains("место_измерения")) {
                forDelete.detach();
            }

            artDavl = artDavl.getChildren().get(0);
            List<Content> buf = artDavl.getContent();
            for (Content el : buf) {
                if (el instanceof Text & el.getValue().contains("место измерения")) {
                    ((Text) el).setText(((Text) el).getText().replace("место измерения", "Место измерения"));
                }
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: make good and nice Arterial pressure.", Console.ANSI_YELLOW);
        }
    }

    //to get good and nice local status
    private static void sixteen(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element mestStat = findElWithNameAndAttr(root, "test", "*:Местный_статус/*:Местный_статус/*:data/*:", "if");
            if (mestStat == null) {
                return;
            }
            List<Content> detach = mestStat.removeContent();
            Element tr = new Element("tr");
            Element td = new Element("td");
            td.setAttribute("class", "myml");
            td.setContent(detach);
            tr.setContent(td);
            addMythInStrongAndBr(tr);
            mestStat.setContent(tr);
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: make good and nice local status.", Console.ANSI_YELLOW);
        }
    }

    //Researches from capital char
    private static void seventeen(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element templIssl = findElWithNameAndAttr(root, "name", "ResearchesFormat", "template");
            if (templIssl == null) {
                return;
            }
            Element neededForEach = findElWithNameAndAttr(templIssl, "select",
                    "*:Сведения_об_исследовании/*:data/*:Любое_событие_as_Point_Event/*", "for-each");
            Element neededIfPos = findElWithNameAndAttr(neededForEach, "test", "position", "if");
            neededIfPos.removeContent();
            neededIfPos.addContent(new Element("br"));

            Element neededValueOf = findElWithNameAndAttr(neededForEach, "select", ".", "value-of");
            neededValueOf.detach();
            Element newCallTemplate = new Element("call-template");
            newCallTemplate.setNamespace(xsl);
            newCallTemplate.setAttribute("name", "string-capltrim");
            Element newWithParam = new Element("with-param");
            newWithParam.setAttribute("name", "string");
            newWithParam.setAttribute("select", ".");
            newWithParam.setNamespace(xsl);
            newCallTemplate.setContent(newWithParam);
            neededForEach.addContent(newCallTemplate);
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: make researches from capital char.", Console.ANSI_YELLOW);
        }
    }

    //Wrap header of general inspection in tr/td
    private static void eighteen(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element tempGenInsp = findElWithNameAndCont(root, "Общий осмотр", "thead");
            tempGenInsp = tempGenInsp.getParentElement();
            Element placeToInsert = tempGenInsp.getParentElement();
            Element tr = new Element("tr");
            Element td = new Element("td");
            td.setContent(tempGenInsp.detach());
            tr.setContent(td);
            placeToInsert.addContent(0, tr);
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: wrap general inspection in tr/td.", Console.ANSI_YELLOW);
        }
    }

    //Change Complication and Concomitant disease headers from part to myth
    private static void nineteen(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element temp = findElWithNameAndCont(root, "сложнение", "span");
            if (temp == null) {
                temp = findElWithNameAndCont(root, "сложнение", "strong");
            }
            temp.setText("Осложнение");
            temp.setAttribute("class", "myth");
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: change Осложнение from part to myth.", Console.ANSI_YELLOW);
        }
        try {
            Element temp = findElWithNameAndCont(root, "Дополнительный диагноз", "span");
            if (temp == null) {
                Console.printMessage("Дополнительный диагноз not found!", Console.ANSI_CYAN);

            } else {
                temp.setAttribute("class", "myth");
            }
            saveXSLT(doc, filePath);
        } catch (Exception ig) {
            Console.printMessage("Failed: change Дополнительный диагноз from part to myth.", Console.ANSI_YELLOW);
        }
    }

    //Replace whitespaces in "In Period" value
    private static void twenty(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element temp = findElWithNameAndAttr(root, "select", "Болеет_в_течение", "with-param");
            temp = temp.getParentElement();

            Element parent = temp.getParentElement();
            int index = parent.getContent().indexOf(temp);
            temp = temp.detach();

            Element variable = new Element("variable");
            variable.setNamespace(xsl);
            variable.setAttribute("name", "date");
            variable.setContent(temp);
            parent.addContent(index, variable);

            Element valueOf = new Element("value-of");
            valueOf.setNamespace(xsl);
            valueOf.setAttribute("select", "replace($date, ' ', ' ')");
            parent.addContent(index + 1, valueOf);
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: replace whitespaces in \"In Period\" value.", Console.ANSI_YELLOW);
        }
    }

    //Delete br in Interpretation
    private static void twentyone(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element element = findElWithNameAndAttr(root, "test", "Интерпретация_результатов_обследования", "if");
            if (element.getChildren().get(0).getName().equals("br")) {
                element.getChildren().remove(0);
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: delete br in interpretation.", Console.ANSI_YELLOW);
        }
    }

    //Make in the moment compl from low case
    private static void twentytwo(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element buf = findElWithNameAndAttr(root, "select", "На_момент_осмотра_жалобы", "with-param");
            if (buf == null) {
                buf = findElWithNameAndAttr(root, "select", "На_момент_осмотра_жалобы", "value-of");
            }
            while (!buf.getName().contains("variable")) {
                buf = buf.getParentElement();
            }
            String nameOfVariable = buf.getAttributeValue("name");
            buf = findElWithNameAndAttr(root, "select", nameOfVariable, "with-param");
            buf.getParentElement().setAttribute("name", "string-ltrim");
            saveXSLT(doc, filePath);
        } catch (Exception ex) {
            Console.printMessage("Failed: make \"In the moment...\" from low char.", Console.ANSI_YELLOW);
        }
    }

    //Remove br from at the next reception
    private static void twentythree(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element buf = findElWithNameAndCont(root, "по последующему", "strong");
            buf = buf.getParentElement().getChildren().get(0);
            if (buf.getName().contains("if")) {
                if (buf.getChildren().size() == 1
                        & buf.getChildren().get(0).getName().equalsIgnoreCase("br")) {
                    buf.detach();
                }
            }
            saveXSLT(doc, filePath);

        } catch (Exception ex) {
            Console.printMessage("Failed: remove br from \"at the next reception\".", Console.ANSI_YELLOW);
        }
    }

    //delete extra whitespace before edizm
    private static void twentyfour(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        boolean deleted;
        do {
            try {
                boolean found = false;
                deleted = false;
                Element edizm = findElWithNameAndAttr(root, "test", "$val", "when")
                        .getParentElement().getParentElement();
                List<Element> elIf = edizm.getChildren();
                if (elIf.get(1).getName().equalsIgnoreCase("if")) {
                    for (Content content : root.getDescendants()) {
                        if (content instanceof Element) {
                            if (((Element) content).getName().equalsIgnoreCase("call-template")) {
                                if (((Element) content).getAttributeValue("name").contains("edizm")) {
                                    List<Element> elementList = content.getParentElement().getChildren();
                                    int contPos = elementList.indexOf(content);
                                    if (contPos == 0) {
                                        continue;
                                    }
                                    if (elementList.get(contPos - 1).getName().contains("span")) {
                                        elementList.remove(contPos - 1);
                                        if (!found) {
                                            Console.printMessage("Warning: found and removed extra whitespaces before edizm!", Console.ANSI_CYAN);
                                            found = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                saveXSLT(doc, filePath);
            } catch (Exception ignored) {
                deleted = true;
            }
        } while (deleted);
    }

    //make gaping before EVALUATION headers
    private static void twentyfive(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        String xsd_path = CasesFunctions.getXsdPath(filePath);
//        Document xsd_doc = useSAXParser(xsd_path);
//        Element xsd_root = xsd_doc.getRootElement();

        while (true) {
            Element td = findElWithNameAndAttr(root, "class", "myml", "td");
            try {
                if (td != null) {
                    boolean partTd = false;
                    for (Content content : td.getDescendants()) {
                        try {
                            if (((Element) content).getAttributeValue("class").equals("part")) {
                                td.setAttribute("class", "mltmp");
                                partTd = true;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    if (partTd) continue;
                    td.removeAttribute("class");
                    String curPath;
                    if (td.getParentElement().getParentElement().getAttributeValue("test").contains("$")) {
                        curPath = td.getParentElement().getParentElement().getParentElement().getAttributeValue("test").trim();
                    } else {
                        curPath = td.getParentElement().getParentElement().getAttributeValue("test").trim();
                    }

                    while (!StringUtils.startsWithAny(curPath, new String[]{"/", ":", "*"})
                            & !Character.isLetter(curPath.charAt(0))) {
                        curPath = curPath.substring(1);
                    }
                    curPath = curPath.replace("(", "");
                    if (curPath.startsWith("not")) {
                        curPath = StringUtils.replaceOnce(curPath, "not", "");
                    }

                    if (curPath.startsWith("count")) {
                        curPath = StringUtils.replaceOnce(curPath, "count", "");
                    }

                    int posSlash = StringUtils.ordinalIndexOf(curPath, "/", 2);
                    if (posSlash != -1) {
                        int firstSlash = StringUtils.ordinalIndexOf(curPath, "/", 1);
                        String parentNode = curPath.substring(0, firstSlash);
                        String childNode = curPath.substring(firstSlash + 1, posSlash);
                        if (parentNode.equals(childNode)) {
                            boolean finded = false;
                            int slash_position = 3;
                            while (!finded) {
                                firstSlash = posSlash;
                                posSlash = StringUtils.ordinalIndexOf(curPath, "/", slash_position);
                                String nowNode = curPath.substring(firstSlash + 1, posSlash);
                                if (nowNode.contains("*:data") | nowNode.contains("Любое_событие")
                                        | nowNode.equals("*:Осмотр") | nowNode.equals("*:Подробности")) {
                                    slash_position++;
                                } else {
                                    finded = true;
                                }
                            }
                        }
                    }
                    int posSpace = curPath.indexOf(" ");
                    int posResult;
                    if (posSlash == -1) {
                        posResult = posSpace;
                    } else if (posSpace == -1) {
                        posResult = posSlash;
                    } else posResult = posSlash < posSpace ? posSlash : posSpace;

                    curPath = curPath.substring(0, posResult);
                    String curPathNode = curPath.substring(curPath.lastIndexOf(":") + 1);
//                    List<String> previousHeads = new ArrayList<>();
//                    Element curNode = findElWithNameAndAttr(xsd_root, "name", curPathNode, "element");
//                    for(Element iterNode : curNode.getParentElement().getChildren()) {
//                        if(iterNode == curNode) {
//                            break;
//                        }
//                        previousHeads.add(iterNode.getAttributeValue("name"));
//                    }
//                    previousHeads = previousHeads.stream().filter(el -> !el.equals("name")).collect(Collectors.toList());

                    Element elForEach = new Element("for-each", xsl);
                    elForEach.setAttribute("select", curPath + "[1]");
                    Element elIf = new Element("if", xsl);
                    elIf.setAttribute("test", "count(preceding-sibling::*) > 1");
                    Element elAttr = new Element("attribute", xsl);
                    elAttr.setAttribute("name", "class");
                    elAttr.setText("myml");
                    elIf.setContent(elAttr);
                    elForEach.setContent(elIf);
                    td.addContent(0, elForEach);
                } else {
                    break;
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        while (true) {
            Element td = findElWithNameAndAttr(root, "class", "mltmp", "td");
            try {
                if (td != null) {
                    td.setAttribute("class", "myml");
                } else {
                    break;
                }
            } catch (Exception ignored) {
            }
        }


        saveXSLT(doc, filePath);
    }

    //all main headers in <tr> <td class=myml>
    private static void twentysix(String filePath, String pattern) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            forEachTd(root);
            deleteEmptyTr(root);

            Element stdRootElement = root.getChildren().get(1).getChild("html").getChild("body").getChild("div").getChildren().get(0).getChildren().get(0);//.get(1).getChildren().get(0).getChildren();//.get(2).getChild("tbody").getChildren();
            Element obsOsm = findHeader(stdRootElement, pattern);
            Element ob = obsOsm;
            try {
                if (ob.getChildren("tr").size() == 1) {
                    ob = obsOsm.getChild("tr");

                    if (!ob.getName().equals("variable")) {
                        ob = ob.getChild("td");
                    }

                    List<Content> temp = ob.removeContent();
                    ob.setContent(new Element("table").setAttribute("align", "left").setContent(new Element("tbody")
                            .setContent((new Element("tr")).setContent((new Element("td")).setContent(temp)))));
                }
            } catch (Exception ignored) {
            }

            forEachStrong(ob);
            forEachStrong(ob);
            forEachStrong(ob.getParentElement().getParentElement());
            forEachStrong(ob.getParentElement());

            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
        }
    }

    private static Element findHeader(Element root, String pattern) {
        try {
            List<Element> listElement = root.getChildren();//.get(1).getChildren().get(0).getChildren();//.get(2).getChild("tbody").getChildren();
            Element header = null;
            for (Element el : listElement) {
                if (header != null) return header;
                List<Attribute> buf = el.getAttributes();
                try {
                    for (Attribute atr : buf) {

                        String atrVal = atr.getValue();
                        if (atrVal.contains(" or ")) {
                            atrVal = atrVal.substring(0, atrVal.indexOf(" or "));
                        }
                        if (atrVal.contains(pattern)
                                & el.getChildren("strong").size() > 0) {
                            header = el;
                            return header;
                        }

                    }
                } catch (Exception ignored) {
                }
            }
            if (header == null) {
                for (Element element : listElement) {
                    header = findHeader(element, pattern);
                    if (header != null) {
                        return header;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    //remove <br/> in anamnesis
    private static void twentyseven(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element brToRemove = null;
            for (Content content : root.getDescendants()) {
                if (content instanceof Element) {
                    if (((Element) content).getName().equals("br")) {
                        Element parent = content.getParentElement();
                        int brPos = parent.getContent().indexOf(content);
                        try {
                            Content prev = parent.getContent().get(brPos - 1);
                            if (prev instanceof Text
                                    & prev.getValue().contains("Анамнез")) {
                                ((Text) prev).setText(prev.getValue() + " ");
                                brToRemove = (Element) content;
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            if (brToRemove != null) {
                brToRemove.detach();
            }
            saveXSLT(doc, filePath);
        } catch (Exception ignored) {
            Console.printMessage("Failed: remove <br/> in anamnesis.", Console.ANSI_YELLOW);
            ignored.printStackTrace();
        }
    }


    private static void twentyeight(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            addMymltoAllTd(root);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        saveXSLT(doc, filePath);
    }

    private static Element findElWithNameAndCont(Element root, String contains, String name) {
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

    private static Element findElWithNameAndAttr(Element root, String attributeName, String attributeValue, String name) {
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

    private static void saveXSLT(Document doc, String path) {
        try {
            XMLOutputter xmlOutputter = new XMLOutputter();
            OutputStream outStream = new FileOutputStream(path);
            xmlOutputter.output(doc, outStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void addMymltoAllTd(Element element) {
        for (Element el : element.getChildren()) {
            addMymltoAllTd(el);
        }
        if (element.getName().equals("td")) {
            Element child = element.getChildren().get(0);
            try {
                if (element.getParentElement().getParentElement().getAttributeValue("test").contains("Общий_осмотр")) {
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (child.getChildren().get(0).getName().equalsIgnoreCase("thead")) {
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                if (child.getName().equalsIgnoreCase("variable")) {
                    if (child.getAttributeValue("name").contains("left_attr")) {
                        return;
                    }
                }
            } catch (Exception ignored) {
            }
            element.setAttribute("class", "myml");
        }
    }

    //Delete all Attributes from all td elements
    private static void forEachTd(Element element) {
        List<String> safePatterns = new ArrayList<>();
        safePatterns.add("padding-bottom");
        if (element.getName().equals("td")) {
            for (Attribute attribute : element.getAttributes()) {
                if (safePatterns.stream().noneMatch(el -> attribute.getValue().contains(el))) {
                    element.setAttributes(null);
                }
            }
        }
        for (Element el : element.getChildren()) {
            forEachTd(el);
        }
    }

    private static void deleteEmptyTd(Element element) {
        List<String> safePatterns = new ArrayList<>();
        safePatterns.add("padding-bottom");

        boolean toDelete = true;
        if (element.getName().equals("td")) {
            if (element.getChildren().isEmpty()) {
                for (Attribute attribute : element.getAttributes()) {
                    String atrVal = attribute.getValue();
                    if (safePatterns.stream().anyMatch(el -> el.contains(atrVal))) {
                        toDelete = false;
                    }
                }
                if (toDelete) {
                    element.detach();
                    return;
                }
            }
        }
        List<Element> buf = element.getChildren();
        if (buf == null) return;
        for (Element element1 : buf) {
            try {
                deleteEmptyTd(element1);
            } catch (Exception ignored) {
            }
        }
    }

    //Delete all empty tr or tr
    private static void deleteEmptyTr(Element element) {

        if (element.getName().equals("tr")) {
            if (element.getChild("td") == null) {
                if (element.getChild("th") == null) {
                    element.detach();
                    return;
                }
            }
        }
        List<Element> buf = element.getChildren();
        if (buf == null) return;
        for (Element element1 : buf) {
            try {
                deleteEmptyTr(element1);
            } catch (Exception ignored) {
            }
        }

    }

    //Get level of In element
    private static int levelOfVariableInIf(Element element) {
        for (Element el : element.getChildren()) {
            if (el.getName().contains("variable")) {
                int res = 0;
                String tmp = el.getAttributeValue("name").replace("content", "")
                        .replace("Up", "").replace("v", "").replace("con", "");
                if (!tmp.startsWith("_")) {
                    res++;
                }
                res += tmp.replaceAll("[0-9]", "").length();
                return res;
            }
        }
        return 0;
    }

    private static void forEachStrong(Element element) {
        if (element.getName().equals("strong") & !isLeft(element)) {
            if (element.getParentElement().getName().equals("if")) {
                int level = levelOfVariableInIf(element.getParentElement());
                if (level < 3) {
                    Element destination = element.getParentElement();

                    Element table = new Element("table");
                    Element tr = new Element("tr");
                    Element td = new Element("td");

                    td.setAttribute("class", "myml");


                    table.setContent(destination.removeContent());
                    td.setContent(table);
                    tr.setContent(td);

                    if (level == 2 || level == 0) {
                        addMythInStrongAndBr(tr);
                    }
                    if (level == 1) {
                        addPartInStrongAndBr(tr);
                    }
                    destination.setContent(tr);
                }
            }
        }
        List<Element> buf = element.getChildren();
        if (buf == null) return;
        for (Element element1 : buf) {
            forEachStrong(element1);
        }
    }

    private static boolean isLeft(Element element) {
        if (element.getName().equals("strong")) {
            //element.setText(element.getText().replace("left", ""));
            return element.getText().contains("left");
        }
        return false;
    }

    // delete costili from left attributes
    private static void deleteCostiliLeft(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();

        while (true) {
            Element strongLeftCostil = findElWithNameAndCont(root, "left", "strong");
            if (strongLeftCostil == null) {
                break;
            } else {
                strongLeftCostil.setText(strongLeftCostil.getText().replace("left", ""));
            }
        }
        saveXSLT(doc, filePath);
    }

    private static void addMythInStrongAndBr(Element element) {
        List<Element> elements;
        if (element.getName().equals("tr")) {
            elements = element.getChild("td").getChildren();
            if (elements.get(0).getName().equals("table") & elements.size() == 1) {
                elements = elements.get(0).getChildren();
            }
        } else {
            elements = element.getChildren();
        }


        for (int i = 0; i < elements.size(); i++) {
            Element buf = elements.get(i);
            if ((buf.getName().equals("strong"))) {
                buf.setAttribute("class", "myth");
                Content textWithOnlyCharacters = buf.getContent(0);
                Text newText = new Text(Processing.deleteAllNonCharacter(textWithOnlyCharacters.getValue()));
                buf.setContent(newText);
                elements.add(i + 1, new Element("br"));
            }

        }
    }

    private static void addPartInStrongAndBr(Element element) {
        List<Element> el;
        if (element.getName().equals("tr")) {
            el = element.getChild("td").getChildren();
            if (el.get(0).getName().equals("table") & el.size() == 1) {
                el = el.get(0).getChildren();
            }
        } else {
            el = element.getChildren();
        }


        for (int i = 0; i < el.size(); i++) {
            Element buf = el.get(i);
            if ((buf.getName().equals("strong"))) {
                buf.setAttribute("class", "part");
                Content textWithOnlyCharacters = buf.getContent(0);
                Text newText = new Text(Processing.deleteAllNonCharacter(textWithOnlyCharacters.getValue()));
                buf.setContent(newText);
                el.add(i + 1, new Element("br"));
            }

        }
    }
}
