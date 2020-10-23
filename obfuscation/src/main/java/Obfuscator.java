import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Obfuscator {
    private Cipher ecipher;
    private Cipher dcipher;

    Obfuscator() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] keyBytes = "password".getBytes();
        init(keyBytes);
    }

    Obfuscator(String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] keyBytes = key.getBytes();
        init(keyBytes);
    }

    private void init(byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
        ecipher = Cipher.getInstance("DES");
        dcipher = Cipher.getInstance("DES");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        dcipher.init(Cipher.DECRYPT_MODE, key);
    }

    public void parse(Element e, boolean type) throws IOException, BadPaddingException, IllegalBlockSizeException {
        final NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                parse((Element) n, type);
            } else {
                if (n.getNodeValue().trim().length() != 0) {
                    String str = type ? obfuscate(n.getNodeValue()) : unobfuscate(n.getNodeValue());
                    n.setNodeValue(str);
                }
            }
        }
    }

    private String obfuscate(String str) throws BadPaddingException, IllegalBlockSizeException {
        byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);
        byte[] enc = ecipher.doFinal(utf8);
        return new sun.misc.BASE64Encoder().encode(enc);
    }

    private String unobfuscate(String str) throws IOException, BadPaddingException, IllegalBlockSizeException {
        byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
        byte[] utf8 = dcipher.doFinal(dec);
        return new String(utf8, StandardCharsets.UTF_8);
    }

}
