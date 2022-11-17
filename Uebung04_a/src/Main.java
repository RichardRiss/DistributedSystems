import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

public class Main {
    Logger logger = null;

    public static void main(String[] args) {



        //Read proto file
        String proto = "";
        Path path = Path.of("./Uebung04_a/src/Logmessage.proto").toAbsolutePath();
        try {
            proto = Files.readString(path);
        } catch (Exception e) {
            System.out.println("Unable to read file.");
            System.out.println(e);
        }


    }

    public void logging_init() {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("LogMessage.proto"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }


        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.ALL);
        logger.
    }


}
