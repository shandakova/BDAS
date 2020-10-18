import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        assertEquals("Expected type of action[-u|-o]  and  filename .\n", outContent.toString());
        outContent.reset();
        args = new String[1];
        Main.main(args);
        assertEquals("Expected type of action[-u|-o]  and  filename .\n", outContent.toString());
    }

    @Test
    public void testMainWithWrongTypeOfAction() {
        String[] args = new String[2];
        args[0] = "very wrong type";
        Main.main(args);
        assertEquals("Available type of action -u (unobfuscate) and -o (obfuscate).\n", outContent.toString());
    }

    @Test
    public void testMainWithErrorFile() {
        String[] args = new String[2];
        args[0] = "-o";
        args[1] = "strange_file.doc";
        Main.main(args);
        assertEquals("File not found.\n", outContent.toString());
    }

    @Test
    public void testMainWithErrorLengthOfKey() {
        String[] args = new String[3];
        args[0] = "-o";
        args[1] = "staff.xml";
        args[2] = "aaa";
        Main.main(args);
        assertEquals("If you want to use own key it should have only 8 letters.\n", outContent.toString());
        outContent.reset();
        args[2] = "aaaaaaaaa";
        Main.main(args);
        assertEquals("If you want to use own key it should have only 8 letters.\n", outContent.toString());
    }

    @Test
    public void testMainWithNormalFileWithoutKey() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("staff.xml"), UTF_8);
            String[] args = new String[2];
            args[0] = "-o";
            args[1] = "staff.xml";
            Main.main(args);
            args[0] = "-u";
            Main.main(args);
            List<String> newLines = Files.readAllLines(Paths.get("staff.xml"), UTF_8);
            assertEquals(lines, newLines);
            assertEquals(0, outContent.toString().length());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testMainWithNormalFileWithKey() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("staff.xml"), UTF_8);
            String[] args = new String[3];
            args[0] = "-o";
            args[1] = "staff.xml";
            args[2] = "aaaaaaaa";
            Main.main(args);
            args[0] = "-u";
            Main.main(args);
            List<String> newLines = Files.readAllLines(Paths.get("staff.xml"), UTF_8);
            assertEquals(lines, newLines);
            assertEquals(0, outContent.toString().length());
        } catch (IOException e) {
            fail();
        }
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }
}