import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.GeneralSecurityException;

public class Main {
    public static void main(String[] argv) {
        if (argv.length < 2) {
            System.out.println("Expected type of action[-u|-o]  and  filename .");
            return;
        }
        if (!argv[0].equals("-o") && !argv[0].equals("-u")) {
            System.out.println("Available type of action -u (unobfuscate) and -o (obfuscate).");
            return;
        }
        if (argv.length == 3 && argv[2].length() != 8) {
            System.out.println("If you want to use own key it should have only 8 letters.");
            return;
        }

        try {
            File fXmlFile = new File(argv[1]);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            Obfuscator obfuscator = (argv.length == 3) ? new Obfuscator(argv[2]) : new Obfuscator();

            obfuscator.parse(doc.getDocumentElement(), argv[0].equals("-o"));

            DOMImplementationLS ls = (DOMImplementationLS) doc.getImplementation()
                    .getFeature("LS", "3.0");
            LSSerializer ser = ls.createLSSerializer();
            ser.getDomConfig().setParameter("format-pretty-print", true);
            OutputStream outStream = new FileOutputStream(argv[1]);
            LSOutput out = ls.createLSOutput();
            out.setByteStream(outStream);
            ser.write(doc, out);
        } catch (ParserConfigurationException | SAXException e) {
            System.out.println("An error occurred while parsing a file");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("Input/Output error occurred.");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

}
