package screenform;

import files.FilesIO;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class JDOMProcessing {
    public static void processXSLT() {
        sixth(FilesIO.out.toString());
        eight(FilesIO.out.toString());
        twenty_two(FilesIO.out.toString());

        recomMyth(FilesIO.out.toString());

        twenty_three(FilesIO.out.toString());

        //twenty_four: deprecated TODO: Need to refactor. Now it works incorrect.
        //twenty_four(FilesIO.out.toString());
        twenty_five(FilesIO.out.toString());
        twenty_six(FilesIO.out.toString());
        twenty_seven(FilesIO.out.toString());
        twenty_eight(FilesIO.out.toString());
        twenty_nine(FilesIO.out.toString());
        thirty(FilesIO.out.toString());
        deleteCostiliLeft(FilesIO.out.toString());
    }

    private static org.jdom2.Document useSAXParser(String fileName) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();

            return saxBuilder.build(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Move obsh diag and soput to begin
    private static void sixth(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        forEachTd(root);
        forEachTr(root);

        Element sopDiag = findElWithNameAndCont(root, "Сопутствующее заболевание", "if");
        Element spanSopDiag = findElWithNameAndCont(sopDiag, "Сопутствующее заболевание", "span");
        spanSopDiag.setAttribute("class", "myth");
        spanSopDiag.setText("Сопутствующий диагноз");
        Comment sopDiagComment = null;
        Element sopDiagDest = findElWithNameAndCont(root, "Сопутствующий диагноз", "tbody");

        int sopDiagIndex = sopDiag.getParent().getContent().indexOf(sopDiag);
        try {
            if (sopDiag.getParent().getContent().get(sopDiagIndex - 2) instanceof Comment) {
                sopDiagComment = (Comment) sopDiag.getParent().getContent().get(sopDiagIndex - 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
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


        int osnDiagIndex = osnDiag.getParent().getContent().indexOf(osnDiag);
        try {
            if (osnDiag.getParent().getContent().get(osnDiagIndex - 2) instanceof Comment) {
                osnDiagComment = (Comment) osnDiag.getParent().getContent().get(osnDiagIndex - 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        osnDiagDest.addContent(0, osnDiag.detach());
        if (osnDiagComment != null) {
            osnDiagDest.addContent(0, new Text("\n"));
            osnDiagDest.addContent(0, osnDiagComment.detach());
            osnDiagDest.addContent(0, new Text("\n"));

        }
        saveXSLT(doc);
    }

    //all main headers in <tr> <td class=myml>
    private static void eight(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            forEachTd(doc.getRootElement());
            forEachTr(root);

            List<Element> listElement = root.getChildren().get(1).getChild("html").getChild("body").getChild("div").getChildren().get(0).getChildren().get(0).getChildren();//.get(1).getChildren().get(0).getChildren();//.get(2).getChild("tbody").getChildren();
            Element obsOsm = new Element("j");
            for (Element el : listElement) {
                List<Attribute> buf = el.getAttributes();
                for (Attribute atr : buf) {
                    if (atr.getValue().contains("*:Общий_осмотр")) obsOsm = el;
                }
            }

            Element ob;
            if (filePath.contains("14974") || filePath.contains("23958")) {
                ob = (Element) obsOsm.getChild("tr").getChild("td").getContent(3); //for surgeon
            } else {
                if (filePath.contains("22954")) {
                    ob = findElWithNameAndAttr(root, "test", "*:Жалобы_и_анамнез_заболевания//*:Жалобы_и_анамнез_заболевания//*:Подробности_истории_болезни", "if");
                } else {
                    ob = obsOsm.getChild("tr");
                }
            }

            if (!filePath.contains("22954")) { //esli est' obs osm
                if (ob.getName() != "variable") {
                    ob = ob.getChild("td");
                }
                List<Content> temp = ob.removeContent();
                ob.setContent(new Element("table").setAttribute("align", "center").setContent(new Element("tbody").setContent(temp)));
            }

            forEachStrong(ob);
            forEachStrong(ob);

            saveXSLT(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //all main headers from capital trim
    private static void twenty_three(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            for (Content el : root.getDescendants()) {
                if (el instanceof Element) {
                    if (((Element) el).getName() == "call-template") {
                        if (((Element) el).getAttributeValue("name").contains("string-ltrim")) {
                            Element withParam = ((Element) el).getChildren().get(0);
                            if (withParam != null) {
                                if (withParam.getName() == "with-param") {
                                    String name = withParam.getAttributeValue("select").replace("$content", "")
                                            .replace("Up", "").replace("$v", "").replaceAll("[0-9]", "");
                                    int urovenVloz = 1;
                                    if (name.length() <= urovenVloz) {
                                        ((Element) el).setAttribute("name", ((Element) el).getAttributeValue("name").replace("string-ltrim", "string-capltrim"));
                                        //el.detach();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            saveXSLT(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //add comment head
    private static void twenty_four(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        Element comment = findElWithNameAndAttr(root, "test", "*:Общий_осмотр/*:Комментарий/", "if");
        if (comment == null) {
            return;
        }
        if (comment == null) {
            return;
        }
        try {
            comment = comment.getChild("tr").getChild("td");
        } catch (Exception e) {
            return;
        }
        if (comment.getChildren().get(0).getText().contains("Комментарий")) {
            return;
        }
        comment.getContent().add(0, new Element("br"));
        comment.getContent().add(0, new Element("strong").setAttribute("class", "myth").setText("Комментарий"));
        saveXSLT(doc);
    }

    //to get good and nice Arterial pressure
    private static void twenty_five(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        Element artDavl = findElWithNameAndAttr(root, "test",
                "*:Общий_осмотр/*:Артериальное_давление/*:data/*:Любое_событие_as_Point_Event/*:data/*:Комментарий/*:value/rm:value != ''", "if");
        if (artDavl == null) {
            return;
        }
        artDavl = artDavl.getChild("tr").getChild("td");
        artDavl = findElWithNameAndAttr(artDavl, "select", "*:Общий_осмотр/*:Артериальное_давление", "for-each");
        artDavl.getChildren().get(0).getChildren().get(0).detach();
        artDavl = artDavl.getChildren().get(0);
        List<Content> buf = artDavl.getContent();
        for (Content el : buf) {
            if (el instanceof Text & el.getValue().contains("место измерения")) {
                ((Text) el).setText(((Text) el).getText().replace("место измерения", "Место измерения"));
            }
        }
        saveXSLT(doc);


    }

    //to get good and nice local status
    private static void twenty_six(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
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
        saveXSLT(doc);
    }

    //Researches from capital char
    private static void twenty_seven(String filePath) {
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
            Namespace xsl = Namespace.getNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");
            Element newCallTemplate = new Element("call-template");
            newCallTemplate.setNamespace(xsl);
            newCallTemplate.setAttribute("name", "string-capltrim");
            Element newWithParam = new Element("with-param");
            newWithParam.setAttribute("name", "string");
            newWithParam.setAttribute("select", ".");
            newWithParam.setNamespace(xsl);
            newCallTemplate.setContent(newWithParam);
            neededForEach.addContent(newCallTemplate);
            System.out.println("TS");
            saveXSLT(doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Wrap header of general inspection in tr/td
    private static void twenty_eight(String filePath) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveXSLT(doc);
    }

    //Change Complication and Concomitant disease headers from part to myth
    private static void twenty_nine(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element temp = findElWithNameAndCont(root, "Осложнение", "span");
            temp.setAttribute("class", "myth");

            temp = findElWithNameAndCont(root, "Дополнительный диагноз", "span");
            temp.setAttribute("class", "myth");
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveXSLT(doc);
    }

    //Replace whitespaces in "In Period" value
    private static void thirty(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        try {
            Element temp = findElWithNameAndAttr(root, "select", "Болеет_в_течение", "with-param");
            temp = temp.getParentElement();

            Element parent = temp.getParentElement();
            int index = parent.getContent().indexOf(temp);
            temp = temp.detach();

            Namespace xsl = Namespace.getNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");

            Element variable = new Element("variable");
            variable.setNamespace(xsl);
            variable.setAttribute("name", "date");
            variable.setContent(temp);
            parent.addContent(index, variable);

            Element valueOf = new Element("value-of");
            valueOf.setNamespace(xsl);
            valueOf.setAttribute("select", "replace($date, ' ', ' ')");
            parent.addContent(index + 1, valueOf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveXSLT(doc);
    }

    //Move parameters from left table to main table
    private static void twenty_two(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        forEachTr(root);

        Element obsOsm = findElWithNameAndCont(root, "Общий осмотр", "span");
        if (obsOsm == null) {
            return;
        }
        while (obsOsm.getName() != "table") {
            obsOsm = obsOsm.getParentElement();
        }
        if (obsOsm.getChild("tbody") != null) {
            obsOsm = obsOsm.getChild("tbody");
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
                el.addContent((new Element("span")).setText(". "));
            }
        }
        Element thead = null;
        try {
            thead = obsOsm.getChild("thead").detach();
        } catch (Exception e) {
        }

        List<Content> contentToMove = obsOsm.removeContent();
        if (thead != null) {
            obsOsm.addContent(thead);
        }


        if (obsOsm.getName() == "tbody") {
            obsOsm.detach();
        }

        Element obsSost = findElWithNameAndCont(root, "Общее", "strong");

        if (obsSost == null) {
            return;
        }
        while (obsSost.getName() != "if") {
            obsSost = obsSost.getParentElement();
        }
        Element obsSostParent = obsSost.getParentElement();
        int obsSostPos = obsSostParent.getContent().indexOf(obsSost);


        obsSostParent.addContent(obsSostPos + 1, (new Element("tr")).setContent((new Element("td")).setContent(contentToMove)));

        saveXSLT(doc);
    }

    //Next visit recommendations to good and nice condition
    private static void recomMyth(String filePath) {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
        forEachTr(root);
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

        saveXSLT(doc);
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
            } catch (Exception e) {
            }
        }
        return null;
    }

    private static void saveXSLT(Document doc) {
        try {
            XMLOutputter xmlOutputter = new XMLOutputter();
            OutputStream outStream = new FileOutputStream(FilesIO.out.toString());
            xmlOutputter.output(doc, outStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Delete all Attributes from all td elements
    private static void forEachTd(Element element) {
        if (element.getName() == "td") {
            element.setAttributes(null);
        }
        for (Element el : element.getChildren()) {
            forEachTd(el);
        }
    }

    //Delete all empty tr or tr/td
    private static void forEachTr(Element element) {
        if (element.getName() == "tr") {
            if (element.getChild("td") == null) {
                if (element.getChild("th") == null) {
                    element.detach();
                    return;
                }
            }
        }
        List<Element> buf = element.getChildren();
        if (buf == null) return;
        for (int i = 0; i < buf.size(); i++) {
            forEachTr(buf.get(i));
        }
    }

    //Get level of In element
    private static int levelOfVariableInIf(Element element) {
        for (Element el : element.getChildren()) {
            if (el.getName().contains("variable")) {
                int res = 0;
                String tmp = el.getAttributeValue("name").replace("content", "")
                        .replace("Up", "").replace("v", "");
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
        if (element.getName() == "strong" & !isLeft(element)) {
            if (element.getParentElement().getName() == "if") {
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
        for (int i = 0; i < buf.size(); i++) {
            forEachStrong(buf.get(i));
        }
    }

    private static boolean isLeft(Element element) {
        if (element.getName() == "strong") {
            //element.setText(element.getText().replace("left", ""));
            return element.getText().contains("left");
        }
        return false;
    }

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
        saveXSLT(doc);
    }

    private static void addMythInStrongAndBr(Element element) {
        List<Element> el;
        if (element.getName() == "tr") {
            el = element.getChild("td").getChildren();
            if (el.get(0).getName() == "table" & el.size() == 1) {
                el = el.get(0).getChildren();
            }
        } else {
            el = element.getChildren();
        }


        for (int i = 0; i < el.size(); i++) {
            Element buf = el.get(i);
            if ((buf.getName() == "strong")) {
                buf.setAttribute("class", "myth");
                Content textWithOnlyCharacters = buf.getContent(0);
                Text newText = new Text(Processing.deleteAllNonCharacter(textWithOnlyCharacters.getValue()));
                buf.setContent(newText);
                el.add(i + 1, new Element("br"));
            }

        }
    }

    private static void addPartInStrongAndBr(Element element) {
        List<Element> el;
        if (element.getName() == "tr") {
            el = element.getChild("td").getChildren();
            if (el.get(0).getName() == "table" & el.size() == 1) {
                el = el.get(0).getChildren();
            }
        } else {
            el = element.getChildren();
        }


        for (int i = 0; i < el.size(); i++) {
            Element buf = el.get(i);
            if ((buf.getName() == "strong")) {
                buf.setAttribute("class", "part");
                Content textWithOnlyCharacters = buf.getContent(0);
                Text newText = new Text(Processing.deleteAllNonCharacter(textWithOnlyCharacters.getValue()));
                buf.setContent(newText);
                el.add(i + 1, new Element("br"));
            }

        }
    }
}
