package webstand;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import org.apache.commons.logging.LogFactory;
import repository.Docx;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.logging.Level;


public class Session {
    private WebClient webClient;
    private String caseName;
    private String caseOpt;
    private String xmlString;
    private String xsltString;
    private String printparamString;
    private String placeholdersString;

    public Session(String caseName) {
        this.caseName = caseName;
        this.caseOpt = Docx.getOptName(caseName);
        initWebClient();
        this.loadCase();
    }

    private void initWebClient() {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);


        webClient = new WebClient(BrowserVersion.FIREFOX_60);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getXmlString() {
        return xmlString;
    }

    public void setXmlString(String xmlString) {
        this.xmlString = xmlString;
    }

    public String getXsltString() {
        return xsltString;
    }

    public void setXsltString(String xsltString) {
        this.xsltString = xsltString;
    }

    public String getPrintparamString() {
        return printparamString;
    }

    public void setPrintparamString(String printparamString) {
        this.printparamString = printparamString;
    }

    public String getPlaceholdersString() {
        return placeholdersString;
    }

    public void setPlaceholdersString(String placeholdersString) {
        this.placeholdersString = placeholdersString;
    }

    public void saveCase() {
        try {
            URL url = new URL("http://doctornew-xslt.emias.solit-clouds.ru/web/save.api");

            WebRequest webRequest = new WebRequest(url, HttpMethod.POST);
            defaultHeaderForRequest(webRequest);
            String req = "{\"document\":\"" + Base64.getEncoder().encodeToString(this.xmlString.getBytes()) + "\"," +
                    "\"name\":\"" + this.caseName + "\"," +
                    "\"placeHolders\":\"" + this.placeholdersString + "\"," +
                    "\"props\":\"" + this.printparamString + "\"," +
                    "\"xslt\":\"" + Base64.getEncoder().encodeToString(this.xsltString.getBytes()) + "\"" + "}";
            webRequest.setRequestBody(req);
            WebResponse webResponse = webClient.loadWebResponse(webRequest);
            webResponse.getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCase() {
        try {
            URL url = new URL("http://doctornew-xslt.emias.solit-clouds.ru/web/load.api");
            WebRequest webRequest = new WebRequest(url, HttpMethod.POST);
            defaultHeaderForRequest(webRequest);
            webRequest.setRequestBody("{\"name\":\"" + this.caseName + "\"}");
            WebResponse webResponse = webClient.loadWebResponse(webRequest);
            getAllRegionsFromResponse(webResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadActualXML() {
        try {
            URL url = new URL("http://doctornew-xslt.emias.solit-clouds.ru/web/index.html");
            URL urlList = new URL("http://doctornew-xslt.emias.solit-clouds.ru/web/gen/getList.api");
            HtmlPage page = webClient.getPage(url);
            WebRequest webRequest = new WebRequest(urlList, HttpMethod.POST);
            defaultHeaderForRequest(webRequest);
//            webRequest.setRequestBody("{\"name\":\"" + this.caseOpt + "\"}");
            WebResponse webResponse = webClient.loadWebResponse(webRequest);
            webClient.loadWebResponseInto(webResponse, webClient.getCurrentWindow());

            if (webResponse.getContentAsString().contains(this.caseOpt)) {
                HtmlSelect select = (HtmlSelect) page.getElementById("optName");
//                HtmlOption htmlOption = new HtmlOption();
//                select.appendOption();
//                select.setSelectedAttribute(option, true);

            }
            HtmlSelect select = (HtmlSelect) page.getElementById("optName");
            HtmlOption option = select.getOptionByValue(this.caseOpt);
            select.setSelectedAttribute(option, true);
            URL urlReq = new URL("http://doctornew-xslt.emias.solit-clouds.ru/web/gen/getModel.api");
            page.save(new File("test.html"));
            getAllRegionsFromResponse(webResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllRegionsFromResponse(WebResponse response) {
        String temp = response.getContentAsString();
        int posName = temp.indexOf("name");
        int posXslt = temp.indexOf("xslt");
        int posDocument = temp.indexOf("document");
        int posPlaceholders = temp.indexOf("placeHolders");
        int posProps = temp.indexOf("props");
        int posError = temp.indexOf("error");

        caseName = temp.substring(posName + 4 + 3, posXslt - 3);

        String tmpXslt = temp.substring(posXslt + 4 + 3, posDocument - 3);
        xsltString = new String(Base64.getDecoder().decode(tmpXslt));

        String tmpDocument = temp.substring(posDocument + 8 + 3, posPlaceholders - 3);
        xmlString = new String(Base64.getDecoder().decode(tmpDocument));

        placeholdersString = temp.substring(posPlaceholders + 12 + 3, posProps - 3);
        printparamString = temp.substring(posProps + 5 + 3, posError - 3);
    }

    private void defaultHeaderForRequest(WebRequest request) {
//        request.setCharset("UTF-8"); // old
        request.setCharset(Charset.forName("UTF-8")); // for new
        request.setAdditionalHeader("Accept", "*/*");
        request.setAdditionalHeader("Content-Type", "application/json");
        request.setAdditionalHeader("Referer", "http://doctornew-xslt.emias.solit-clouds.ru/web/index.html");
        request.setAdditionalHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
        request.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
    }
}
