package services.spring;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.*;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CurrenciesRetriever {

    private Logger logger = Logger.getLogger(getClass());

    private static final String CURRENCIES_XML_URL =
            "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    private Map<String, Double> currencies = Collections.emptyMap();
    private boolean hasBeenRetrieved = false;

    private final Map<String, Double> convertXmlToJavaObj(Document doc) {
        Map<String, Double> currencies = new TreeMap<>();

        NodeList nodes = doc.getElementsByTagName("Cube");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                if (elem.hasAttribute("currency") && elem.hasAttribute("rate")) {
                    currencies.put(
                        elem.getAttribute("currency"),
                        Double.valueOf(elem.getAttribute("rate"))
                    );
                }
            }
        }

        return currencies;
    }

    private Document createXmlDoc(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            return document;
        } catch (Exception e) {
            logger.error("Exception occurred while constructing XML doc from string", e);
            return null;
        }
    }

    private String downloadXML() {
        try {
            URL website = new URL(CURRENCIES_XML_URL);
            InputStream input = website.openStream();
            return IOUtils.toString(input);
        } catch (Exception e) {
            logger.error("Exception occurred while constructing XML doc from string", e);
            return null;
        }
    }

    public void reset() {
        currencies.clear();
        hasBeenRetrieved = false;
    }

    public Map<String, Double> getCurrencies() {
        if (!hasBeenRetrieved) {
            String xmlString = downloadXML();
            Document xml = createXmlDoc(xmlString);
            currencies = convertXmlToJavaObj(xml);
            hasBeenRetrieved = true;
        }
        return currencies;
    }

    public List<domain.Currency> getCurrenciesAsList() {
        Map<String, Double> map = getCurrencies();
        List<domain.Currency> currencies = new ArrayList<>();

        for (Map.Entry<String, Double> entry : map.entrySet()) {
            currencies.add(new domain.Currency(entry.getKey(), entry.getValue()));
        }

        return currencies;
    }

}
