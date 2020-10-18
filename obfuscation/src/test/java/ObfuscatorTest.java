import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class ObfuscatorTest {
    private final String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<note>\n" +
            "<to>Tove</to>\n" +
            "<from>Jani</from>\n" +
            "<heading>Reminder</heading>\n" +
            "<body>Don't forget me this weekend!</body>\n" +
            "</note>";
    private DocumentBuilder dBuilder;

    @Before
    public void init() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            fail();
        }
    }

    @Test
    public void testObfuscatorWithoutKey() {
        try {
            Obfuscator obfuscator = new Obfuscator();
            InputStream stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            obfuscator.parse(doc.getDocumentElement(), true);
            String newXML = getStringFromXML(doc);
            assertNotEquals(newXML.replace("\n", ""), str.replace("\n", ""));
            obfuscator.parse(doc.getDocumentElement(), false);
            newXML = getStringFromXML(doc);
            assertEquals(newXML.replace("\n", ""), str.replace("\n", ""));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testObfuscatorWithKey() {
        try {
            Obfuscator obfuscator = new Obfuscator("aaaaaaaa");
            InputStream stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            obfuscator.parse(doc.getDocumentElement(), true);
            String newXML = getStringFromXML(doc);
            assertNotEquals(newXML.replace("\n", ""), str.replace("\n", ""));
            obfuscator.parse(doc.getDocumentElement(), false);
            newXML = getStringFromXML(doc);
            assertEquals(newXML.replace("\n", ""), str.replace("\n", ""));
        } catch (Exception e) {
            fail();
        }
    }

    private String getStringFromXML(Document doc) throws TransformerException {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }

}