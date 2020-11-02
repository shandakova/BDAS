import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Expected type of action[-d|-e|-s|-v]  and  filename .");
            return;
        }
        if (!args[0].equals("-e") && !args[0].equals("-d") && !args[0].equals("-s") && !args[0].equals("-v")) {
            System.out.println("Available type of action -e (encrypt), -d (decrypt), -s (sign), -v (verify).");
            return;
        }
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(args[1]));
            byte[] resultsBytes;
            BouncyCastle bc = new BouncyCastle();
            switch (args[0]) {
                case "-e":
                    resultsBytes = bc.encryptBytes(fileBytes);
                    break;
                case "-d":
                    resultsBytes = bc.decryptBytes(fileBytes);
                    break;
                case "-s":
                    resultsBytes = bc.signBytes(fileBytes);
                    break;
                case "-v":
                    boolean isBytesSigned = bc.verifySignedBytes(fileBytes);
                    String message = isBytesSigned ? "Bytes are signed!" : "Bytes are not signed!";
                    System.out.println(message);
                    return;
                default:
                    System.out.println("Unexpected value: " + args[0]);
                    return;
            }
            Path path = Paths.get(args[1]);
            Files.write(path, resultsBytes);
        } catch (NoSuchFileException e) {
            System.out.println("File not founded!");
        } catch (IOException e) {
            System.out.println("Input/Output error occurred.Check your file.");
        } catch (Exception e) {
            System.out.println("Bouncy Castle error!");
        }
    }
}
