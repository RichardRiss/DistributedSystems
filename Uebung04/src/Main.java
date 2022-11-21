import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

public class Main {
    public static void main(String[] args) {
        SocketServer server = new SocketServer();
        SocketClient client = new SocketClient("http://localhost/");

        client.log_msg(1, "Test message", "myself");
    }
}
