import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testMainWithLessThen2Argument() {
        String[] args = new String[0];
        Main.main(args);
        assertEquals("Expected type of action[-d|-e|-s|-v]  and  filename .\n", outContent.toString());
    }

    @Test
    public void testMainWithWrongTypeOfAction() {
        String[] args = new String[2];
        args[0] = "very wrong type";
        Main.main(args);
        assertEquals("Available type of action -e (encrypt), -d (decrypt), -s (sign), -v (verify).\n",
                outContent.toString());
    }

    @Test
    public void testMainWithErrorFile() {
        String[] args = new String[2];
        args[0] = "-e";
        args[1] = "strange_file.doc";
        Main.main(args);
        assertEquals("File not founded!\n",
                outContent.toString());
    }

    @Test
    public void testEncryptionAndDecryption() throws IOException {
        String[] args = new String[2];
        args[0] = "-e";
        args[1] = "test_file_1.txt";
        File file = createFile(args[1]);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Hello");
        }
        Main.main(args);
        args[0] = "-d";
        Main.main(args);
        List<String> strings = Files.readAllLines(Paths.get(args[1]));
        String str = String.join("", strings);
        assertEquals("Hello", str);
        deleteFile(file);
    }

    @Test
    public void testSignAndVerification() throws IOException {
        String[] args = new String[2];
        args[0] = "-s";
        args[1] = "test_file_2.txt";
        File file = createFile(args[1]);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Hello");
        }
        Main.main(args);
        args[0] = "-v";
        Main.main(args);
        assertEquals("Bytes are signed!\n", outContent.toString());
        deleteFile(file);
    }

    @Test
    public void testVerificationWithoutSign() throws IOException {
        String[] args = new String[2];
        args[0] = "-v";
        args[1] = "test_file_3.txt";
        File file = createFile(args[1]);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Hello");
        }
        Main.main(args);
        assertEquals("Bytes are not signed!\n", outContent.toString());
        deleteFile(file);
    }

    private File createFile(String filename) throws IOException {
        File myObj = new File(filename);
        myObj.createNewFile();
        return myObj;
    }

    private void deleteFile(File file) {
        file.delete();
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

}