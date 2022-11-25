import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

public class Main {
    public static void main(String[] args) {
        new Thread(new SocketServer()).start();
        SocketClient client = new SocketClient();
        client.create_log("localhost", 80,"Testtestest");

    }
}
