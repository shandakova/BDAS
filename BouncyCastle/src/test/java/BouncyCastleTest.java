import org.junit.Test;

import static org.junit.Assert.*;

public class BouncyCastleTest {
    BouncyCastle bc;

    {
        try {
            bc = new BouncyCastle();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void encryptAndDecryptBytes() {
        String str = "Test";
        try {
            byte[] encryptedBytes = bc.encryptBytes(str.getBytes());
            assertNotEquals(str, new String(encryptedBytes));
            byte[] decryptedBytes = bc.decryptBytes(encryptedBytes);
            assertEquals(str, new String(decryptedBytes));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void verifySignedBytes() {
        String str = "test";
        byte[] signedBytes;
        try {
            signedBytes = bc.signBytes(str.getBytes());
            assertTrue(bc.verifySignedBytes(signedBytes));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void verifyNotSignedBytes() {
        String str = "test";
        assertFalse(bc.verifySignedBytes(str.getBytes()));
    }

}