package webstand;

import com.gargoylesoftware.htmlunit.*;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;


public class Session {
    private WebClient webClient;
    private String caseName;
    private String xmlString;
    private String xsltString;
    private String printparamString;
    private String placeholdersString;

    public Session(String caseName) {
        this.caseName = caseName;
        initWebClient();
        this.loadCase();
    }

    private void initWebClient() {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);


        webClient = new WebClient(BrowserVersion.CHROME);
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
        request.setCharset("UTF-8");
        request.setAdditionalHeader("Accept", "*/*");
        request.setAdditionalHeader("Content-Type", "application/json");
        request.setAdditionalHeader("Referer", "http://doctornew-xslt.emias.solit-clouds.ru/web/index.html");
        request.setAdditionalHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
        request.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
    }
}
