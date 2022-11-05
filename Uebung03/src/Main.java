public class Main {
    public static void main(String[] args) {
        SocketServer server = new SocketServer();
        SocketClient client = new SocketClient("http://localhost:80");

        client.getSize();


    }
}