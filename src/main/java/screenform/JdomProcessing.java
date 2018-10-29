package screenform;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class JdomProcessing {
    public static int capltrimVal = 0;
    public static List<Element> strongsForCapltrim = new ArrayList<>();

    public static void processXSLT() throws Exception {
        sixth(FilesIO.out.toString()); //refactored
        eightTwoDouble(FilesIO.out.toString());

//        recomMythJDOM(FilesIO.out.toString()); //refactored
//        twentyTwoJDOM(FilesIO.out.toString()); //refactored
//        twenty_three(FilesIO.out.toString());
//        twenty_four(FilesIO.out.toString());
//        twenty_five(FilesIO.out.toString());
//        twenty_six(FilesIO.out.toString());
//        twenty_seven(FilesIO.out.toString());

    }

    public static String prettyFormat(String input) {
        return prettyFormat(input, "2");
    }

    public static String prettyFormat(String input, String indent) {
        Source xmlInput = new StreamSource(new StringReader(input));
        StringWriter stringWriter = new StringWriter();
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", indent);
            transformer.transform(xmlInput, new StreamResult(stringWriter));

            String pretty = stringWriter.toString();
            pretty = pretty.replace("\r\n", "\n");
            return pretty;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void preprocess() throws Exception {
        String doc = Files.readAllLines(new File(FilesIO.input).toPath()).toString();
        String newText = prettyFormat(doc);
        Files.write(new File(FilesIO.input).toPath(), newText.getBytes());
    }

    public static org.jdom2.Document useSAXParser(String fileName) throws JDOMException,
            IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(new File(fileName));
    }


    //Move obsh diag and soput to begin
    public static void sixth(String filePath) throws Exception {
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
        }
        osnDiagDest.addContent(0, osnDiag.detach());
        if (osnDiagComment != null) {
            osnDiagDest.addContent(0, new Text("\n"));
            osnDiagDest.addContent(0, osnDiagComment.detach());
            osnDiagDest.addContent(0, new Text("\n"));

        }
        saveXSLT(doc);
    }

    public static void eightGood(String filePath) throws Exception {
        Document doc = useSAXParser(filePath);
        forEachTd(doc.getRootElement());
        Element root = doc.getRootElement();

        forEachTr(root);
        List<Element> listElement = root.getChildren().get(1).getChild("html").getChild("body").getChild("div").getChildren().get(0).getChildren().get(0).getChildren();//.get(1).getChildren().get(0).getChildren();//.get(2).getChild("tbody").getChildren();
        Element obsOsm = new Element("j");
        for (Element el : listElement) {
            List<Attribute> buf = el.getAttributes();
            for (Attribute atr : buf) {
                if (atr.getValue().contains("*:Общий_осмотр")) obsOsm = el;
            }
        }

        Element ob = null;
        if (filePath.contains("14974")) {
            ob = (Element) obsOsm.getChild("tr").getChild("td").getContent(3); //for surgeon
        } else {
            if (filePath.contains("22954")) {
                ob = findElWithNameAndAttr(root, "test", "*:Жалобы_и_анамнез_заболевания//*:Жалобы_и_анамнез_заболевания//*:Подробности_истории_болезни", "if");
            } else {
                ob = obsOsm.getChild("tr").getChild("td");
            }
        }
        List<Content> firstBlocks;
        if (!filePath.contains("22954")) { //esli est' obs osm
            List<Content> temp = ob.removeContent();
            ob.setContent(new Element("table").setAttribute("align", "center").setContent(new Element("tbody").setContent(temp)));
            firstBlocks = ob.getChild("table").getChild("tbody").getContent();
        } else {
            firstBlocks = ob.getChild("tr").getChild("td").getContent();
        }
        boolean first = true; // Первый блок без отступа
        boolean hasSpan = false;

        for (int firstIter = 0; firstIter < firstBlocks.size(); firstIter++) {
            if (firstBlocks.get(firstIter) instanceof Element) {
                List<Content> secondBlocks = ((Element) firstBlocks.get(firstIter)).getContent();

                for (int secondIter = 0; secondIter < secondBlocks.size(); secondIter++) {
                    if (secondBlocks.get(secondIter) instanceof Element) {
                        List<Content> thirdBlocks = ((Element) secondBlocks.get(secondIter)).getContent();

                        for (int thirdIter = 0; thirdIter < thirdBlocks.size(); thirdIter++) {
                            if (thirdBlocks.get(thirdIter) instanceof Element) {
                                forEachStrong((Element) thirdBlocks.get(thirdIter));
                            }
                        }
                        forEachStrong((Element) secondBlocks.get(secondIter));
                    }
                }
                forEachStrong((Element) firstBlocks.get(firstIter));
            }
        }
        saveXSLT(doc);
    }

    public static void iteratingFindIf(Element root) {

    }


    //For each main punkt
    public static void eightTwo(String filePath) throws Exception {
        Document doc = useSAXParser(filePath);
        forEachTd(doc.getRootElement());
        Element root = doc.getRootElement();

        forEachTr(root);
        List<Element> listElement = root.getChildren().get(1).getChild("html").getChild("body").getChild("div").getChildren().get(0).getChildren().get(0).getChildren();//.get(1).getChildren().get(0).getChildren();//.get(2).getChild("tbody").getChildren();
        Element obsOsm = new Element("j");
        for (Element el : listElement) {
            List<Attribute> buf = el.getAttributes();
            for (Attribute atr : buf) {
                if (atr.getValue().contains("*:Общий_осмотр")) obsOsm = el;
            }
        }

        Element ob = null;
        if (filePath.contains("14974")) {
            ob = (Element) obsOsm.getChild("tr").getChild("td").getContent(3); //for surgeon
        } else {
            if (filePath.contains("22954")) {
                ob = findElWithNameAndAttr(root, "test", "*:Жалобы_и_анамнез_заболевания//*:Жалобы_и_анамнез_заболевания//*:Подробности_истории_болезни", "if");
            } else {
                ob = obsOsm.getChild("tr").getChild("td");
            }
        }
        List<Content> blocks;
        if (!filePath.contains("22954")) { //esli est' obs osm
            List<Content> temp = ob.removeContent();
            ob.setContent(new Element("table").setAttribute("align", "center").setContent(new Element("tbody").setContent(temp)));
            blocks = ob.getChild("table").getChild("tbody").getContent();
        } else {
            blocks = ob.getChild("tr").getChild("td").getContent();
        }
        boolean first = true; // Первый блок без отступа
        boolean hasSpan = false; // Если наш стронг окружен спаном
        for (int i = 0; i < blocks.size(); i++) {
            Content buf = blocks.get(i);
            if (buf instanceof Element) {
                if (((Element) buf).getName() == "if") {
                    // check for <span>. <strong>...
                    Element checkSpan = (Element) blocks.get(i);
                    if ((checkSpan.getChildren().get(0).getName() == "span") & (checkSpan.getChildren().get(0).getChild("strong") != null)) {
                        Element span = (checkSpan.getChildren().get(0)).detach();
                        checkSpan.getChildren().add(0, span.getChild("strong").detach());
                        hasSpan = true;
                    } else if (checkSpan.getChildren().get(0).getName() == "span") {
                        checkSpan.getChildren().get(0).setName("strong");
                    }
                    Element tmpStrongEnd = ((Element) blocks.get(i));
                    if (tmpStrongEnd.getChildren().get(tmpStrongEnd.getChildren().size() - 1).getName() == "if") {
                        Element localTmp = tmpStrongEnd.getChildren().get(tmpStrongEnd.getChildren().size() - 1);
                        try {
                            while (!localTmp.getName().contains("strong")) {
                                localTmp = localTmp.getChildren().get(0);
                            }
                            addMythInStrongAndBr(localTmp.getParentElement());
                        } catch (Exception e) {
                        }
                    } else {
                        List<Content> blocksSecond = ((Element) buf).getContent();
                        for (int iSecond = 0; iSecond < blocksSecond.size(); iSecond++) {
                            Content bufSecond = blocksSecond.get(iSecond);
                            if (bufSecond instanceof Element) {
                                if (((Element) bufSecond).getName() == "if") {
                                    // check for <span>. <strong>...
                                    Element checkSpanSecond = (Element) blocksSecond.get(iSecond);
                                    if ((checkSpanSecond.getChildren().get(0).getChild("strong") != null)) {
                                        Element span = (checkSpanSecond.getChildren().get(0)).detach();
                                        checkSpanSecond.getChildren().add(0, span.getChild("strong").detach());
                                        hasSpan = true;
                                    }
                                    Element tmpStrongEndSecond = ((Element) blocksSecond.get(iSecond));
                                    if (tmpStrongEndSecond.getChildren().get(tmpStrongEndSecond.getChildren().size() - 1).getName() == "if") {
                                        Element localTmp = tmpStrongEndSecond.getChildren().get(tmpStrongEndSecond.getChildren().size() - 1);
                                        try {
                                            while (!localTmp.getName().contains("strong")) {
                                                localTmp = localTmp.getChildren().get(0);
                                            }
                                            addMythInStrongAndBr(localTmp.getParentElement());
                                        } catch (Exception e) {
                                        }
                                    }

                                }
                            }
                        }
                    }

                    {
                        List<Content> blocksSecond = ((Element) buf).getContent();
                        for (int iSecond = 0; iSecond < blocksSecond.size(); iSecond++) {
                            Content bufSecond = blocksSecond.get(iSecond);
                            if (bufSecond instanceof Element) {
                                if (((Element) bufSecond).getName() == "if") {
                                    // check for <span>. <strong>...
                                    Element checkSpanSecond = (Element) blocksSecond.get(iSecond);
                                    if ((checkSpanSecond.getChildren().get(0).getChild("strong") != null)) {
                                        Element span = (checkSpanSecond.getChildren().get(0)).detach();
                                        checkSpanSecond.getChildren().add(0, span.getChild("strong").detach());
                                        hasSpan = true;
                                    }
                                    Element tmpStrongEndSecond = ((Element) blocksSecond.get(iSecond));
                                    if (tmpStrongEndSecond.getChildren().get(tmpStrongEndSecond.getChildren().size() - 1).getName() == "if") {
                                        Element localTmp = tmpStrongEndSecond.getChildren().get(tmpStrongEndSecond.getChildren().size() - 1);
                                        try {
                                            while (!localTmp.getName().contains("strong")) {
                                                localTmp = localTmp.getChildren().get(0);
                                            }
                                            addMythInStrongAndBr(localTmp.getParentElement());
                                        } catch (Exception e) {
                                        }
                                    }

                                }
                            }
                        }

                    }


                    List<Content> detach = ((Element) blocks.get(i)).removeContent();
                    Element tr = new Element("tr");
                    Element td = new Element("td");
                    if (!first) {
                        td.setAttribute("class", "myml");
                    } else {
                        first = false;
                    }
                    td.setContent(detach);
                    tr.setContent(td);
                    addMythInStrongAndBr(tr);
                    ((Element) blocks.get(i)).setContent(tr);
                }
                if (((Element) buf).getName() == "table") {
                    ob.addContent(0, buf.detach());
                    i--;
                }
            }
        }
        saveXSLT(doc);
        return;
    }

    public static void eightTwoDouble(String filePath) throws Exception {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
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

        Element ob = null;
        if (filePath.contains("14974") || filePath.contains("23958")) {
            ob = (Element) obsOsm.getChild("tr").getContent(3); //for surgeon
        } else {
            if (filePath.contains("22954")) {
                ob = findElWithNameAndAttr(root, "test", "*:Жалобы_и_анамнез_заболевания//*:Жалобы_и_анамнез_заболевания//*:Подробности_истории_болезни", "if");
            } else {
                ob = obsOsm.getChild("tr");
            }
        }
        List<Content> blocks;
        if (!filePath.contains("22954")) { //esli est' obs osm
            ob = ob.getChild("td");
            List<Content> temp = ob.removeContent();
            ob.setContent(new Element("table").setAttribute("align", "center").setContent(new Element("tbody").setContent(temp)));
            blocks = ob.getChild("table").getChild("tbody").getContent();
        } else {
            blocks = ob.getContent();
        }
        boolean first = true; // Первый блок без отступа
        boolean hasSpan = false; // Если наш стронг окружен спаном


        forEachStrong(ob);
        forEachStrongFirst(ob);
        //forEachStrongFirst(ob);

        //withoutStrongsToCapltrim();


        saveXSLT(doc);
    }

    //all main punkt from capital trim
    public static void twenty_three(String filePath) throws Exception {
        Document doc = useSAXParser(filePath);
        Element root = doc.getRootElement();
//        List<Element> elementsToCapltrim = getElsToCapltrim(root);
//        elementsToCapltrim.size();
//        for(Element el : elementsToCapltrim) {
//            Element buf = el.getParentElement();
//            while(buf.getName() != "call-template") {
//                buf = buf.getParentElement();
//
//                if (buf == null) break;
//            }
//                String name = buf.getAttribute("name").getValue();
//            if(!name.contains("capltrim")) {
//                buf.setAttribute("name", name.replace("ltrim","capltrim"));
//            }
//        }

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
    }

    //add comment head
    public static void twenty_four(String filePath) throws Exception {
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

    public static void twenty_five(String filePath) throws Exception {
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

    public static void twenty_six(String filePath) throws Exception {
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

    public static void twenty_seven(String filePath) throws Exception {
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
        }
    }

    public static void twentyTwoJDOM(String filePath) throws Exception {
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

    public static void recomMythJDOM(String filePath) throws Exception {
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

        recomNextStrongEl.setText(deleteAllNonCharecter(recomNextStrongEl.getValue()));
        recomNextStrongEl.setAttribute("class", "myth");

        List<Content> toFindDot = recomNextStrongEl.getParentElement().getContent();
        for (int i = 0; i < toFindDot.size(); i++) {
            if (toFindDot.get(i).equals(recomNextStrongEl)) {
                if (toFindDot.get(i + 1) instanceof Text) {
                    ((Text) toFindDot.get(i + 1)).setText(deleteAllNonCharecter(((Text) toFindDot.get(i + 1)).getText()));
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

    public static List<Element> getElsToCapltrim(Element root) {
        List<Element> elements = new ArrayList<>();
        for (Content el : root.getDescendants()) {
            try {
                if (((Element) el).getAttribute("name").toString().contains("string") & (((Element) el).getName().contains("with-param"))) {
                    Element buf = (Element) el;
                    if (buf.getAttribute("select").getValue().contains("noTag")) {
                        elements.add((Element) el);
                    } else {
                        while (buf.getName() != "variable") {
                            buf = buf.getParentElement();
                        }
                        String bufStr = "";
                        if (buf.getAttribute("name").getValue().contains("vt")) {
                            bufStr = buf.getAttribute("name").getValue().replace("vt", "").replaceAll("[0-9]", "");

                        } else {
                            bufStr = buf.getAttribute("name").getValue().replace("content", "").replaceAll("[0-9]", "");
                        }
                        if (bufStr.length() == 1) {
                            elements.add((Element) el);
                        }
                        if (bufStr == "noTag") {
                            elements.add((Element) el);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        return elements;
    }

    public static Element findElWithNameAndCont(Element root, String contains, String name) {
        for (Content el : root.getDescendants()) {
            try {
                if (el instanceof Text) {
                    if (((Text) el).getText().contains(contains) & name.contentEquals("Text")) {
                        return el.getParentElement();
                    }
                }
                if (el.getValue().contains(contains) & (((Element) el).getName().contains(name))) {
                    return (Element) el;
                }
            } catch (Exception e) {
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
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static void saveXSLT(Document doc) throws Exception {
        XMLOutputter xmlOutputter = new XMLOutputter();
        OutputStream outStream = new FileOutputStream(FilesIO.out.toString());
        xmlOutputter.output(doc, outStream);
    }

    public static void forEachTd(Element element) {
        if (element.getName() == "td") {
            element.setAttributes(null);
        }
        for (Element el : element.getChildren()) {
            forEachTd(el);
        }
    }

    public static void forEachTr(Element element) {
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

    public static int levelOfVariableInIf(Element element) {
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

    public static void forEachStrongFirst(Element element) {
        if (element.getName() == "strong") {
            if (element.getParentElement().getName() == "if") {
                if (strongsForCapltrim.indexOf(element) == -1) {
                    strongsForCapltrim.add(element);
                }
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
            forEachStrongFirst(buf.get(i));
        }
    }

    public static void forEachStrong(Element element) {
        if (element.getName() == "strong") {
            if (element.getParentElement().getName() == "if") {
                if (strongsForCapltrim.indexOf(element) == -1) {
                    strongsForCapltrim.add(element);
                }
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
            forEachStrongFirst(buf.get(i));
        }
    }

    public static void withoutStrongsToCapltrim() {
        for (Element strong : strongsForCapltrim) {
            Element destination = strong.getParentElement();
            List<Content> contentList = destination.getContent();
            List<Content> detached = new ArrayList<>();
            for (int i = 0; i < contentList.size(); i++) {
                Content buf = contentList.get(i);
                if (buf instanceof Element) {
                    if (((Element) buf).getName() == "if") {
                        detached.add(buf.detach());
                        break;
                    }
                }
            }

            Element variable = addAndSetContentListToVariable(destination, detached, 1, capltrimVal++);
            addCallTemplateCapltrimVariable(variable);
        }
    }

    public static Element addAndSetContentListToVariable(Element destination, List<Content> contentList, int posToInsert, int numberOfVar) {
        Namespace xsl = Namespace.getNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");
        Element variableElement = new Element("variable");
        variableElement.setNamespace(xsl);
        variableElement.setAttribute("name", "varCapltrim" + numberOfVar);
        variableElement.setContent(contentList);
        destination.addContent(posToInsert + 1, variableElement);
        return variableElement;
    }

    public static void addCallTemplateCapltrimVariable(Element variable) {
        Namespace xsl = Namespace.getNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");
        Element callTemplateElement = new Element("call-template");
        callTemplateElement.setNamespace(xsl);
        callTemplateElement.setAttribute("name", "string-capltrim");
        Element newWithParamElement = new Element("with-param");
        newWithParamElement.setAttribute("name", "string");
        newWithParamElement.setAttribute("select", "$" + variable.getAttributeValue("name"));
        newWithParamElement.setNamespace(xsl);
        callTemplateElement.setContent(newWithParamElement);
        int posToInsert = variable.getParentElement().getContent().indexOf(variable) + 1;
        variable.getParentElement().addContent(posToInsert, callTemplateElement);
    }


    public static boolean elementIsHead2nd(Element element) {
        List<Element> childs = element.getChildren();
        return true; //for debug
//        for(Element el : childs) {
//            String elNameAtr = "";
//            try{
//                elNameAtr = el.getAttributeValue("name");
//                if(elNameAtr.length() - elNameAtr.replace("_","").length() == 1) {
//                    return true;
//                }
//            }
//            catch (Exception e) {
//                continue;
//            }
//
//        }
//        return false;
    }

    public static void addMythInStrongAndBr(Element element) {
        List<Element> el = null;
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
            if ((buf.getName() == "strong") & (elementIsHead2nd(buf))) {
                buf.setAttribute("class", "myth");
                Content textWithOnlyCharacters = buf.getContent(0);
                Text newText = new Text(deleteAllNonCharecter(textWithOnlyCharacters.getValue()));
                buf.setContent(newText);
                el.add(i + 1, new Element("br"));
            }

        }
//        if(el.get(1).getName() != "br") {
//            el.add(1,new Element("br"));
//        }
    }

    public static void addPartInStrongAndBr(Element element) {
        List<Element> el = null;
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
            if ((buf.getName() == "strong") & (elementIsHead2nd(buf))) {
                buf.setAttribute("class", "part");
                Content textWithOnlyCharacters = buf.getContent(0);
                Text newText = new Text(deleteAllNonCharecter(textWithOnlyCharacters.getValue()));
                buf.setContent(newText);
                el.add(i + 1, new Element("br"));
            }

        }
//        if(el.get(1).getName() != "br") {
//            el.add(1,new Element("br"));
//        }
    }


    public static String deleteAllNonCharecter(String str) {
        return str.replace(":", "").replace(".", "");
    }
}
