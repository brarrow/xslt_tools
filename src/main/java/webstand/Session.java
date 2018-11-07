package webstand;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;



public class Session {
    WebClient webClient;
    HtmlPage page;
    DomElement nameCaseInp;
    DomElement load;
    DomElement save;
    DomElement getHtml;
    DomElement getPdf;
    String caseName;
    String xmlString;
    String xsltString;
    String printparamString;
    String placeholdersString;

    private void initWebClient() {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);


        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    }

    public Session(String caseName) throws Exception {
        this.caseName = caseName;
        initWebClient();
        loadCase();


    }

    public void loadCase() throws Exception{
        URL url = new URL("http://doctornew-xslt.emias.solit-clouds.ru/web/load.api");
        WebRequest requestSettings = new WebRequest(url, HttpMethod.POST);
        requestSettings = defaultHeaderForRequest(requestSettings);
        requestSettings.setRequestBody("{\"name\":\""+this.caseName+"\"}");
        WebResponse redirectPage = webClient.loadWebResponse(requestSettings);
        getAllRegionsFromResponse(redirectPage);
    }

    public void getAllRegionsFromResponse(WebResponse response) {
        String temp = response.getContentAsString();
        String[] temps = temp.split("[:\",\":][:\":\":]");
        temps.clone();
    }

    public WebRequest defaultHeaderForRequest(WebRequest request) {
        request.setAdditionalHeader("Accept", "*/*");
        request.setAdditionalHeader("Content-Type", "application/json");
        request.setAdditionalHeader("Referer", "http://doctornew-xslt.emias.solit-clouds.ru/web/index.html");
        request.setAdditionalHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
        request.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
        return request;
    }
}
