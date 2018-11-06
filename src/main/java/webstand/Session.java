package webstand;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.logging.Level;

public class Session {
    WebClient webClient;
    HtmlPage page;
    DomElement load;
    DomElement save;
    DomElement getHtml;
    DomElement getPdf;
    boolean loadCase;
    boolean saveCase;
    String caseName;
    String xmlString;
    String xsltString;
    String printparamString;
    String placeholdersString;

    public Session(String caseName) throws Exception {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);


        page = webClient.getPage("http://doctornew-xslt.emias.solit-clouds.ru/web/index.html");
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        List<DomElement> list = page.getElementsByTagName("input");
        for (DomElement el : list) {
            String onclickAttrVal = el.getAttribute("onclick");
            if (onclickAttrVal.contains("save")) {
                save = el;
            }
        }

    }
}
