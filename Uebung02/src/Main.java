public class Main {
    public static void main(String[] args) {
        SocketClient client = new SocketClient();
        SocketServer server = new SocketServer();

        //Client with basic socket class
        //client.normal_socket("www.fh-wedel.de", 80);

        //Client with URL socket class
        //client.url_socket("http://www.fh-wedel.de/index.html");

        //Client with proxy
        //client.url_socket("http://www.fh-wedel.de/index.html", "92.222.237.109", 8888);

        //local Server with local socket
        client.normal_socket("localhost", 80);


        //local Server with URL socket class
        //client.url_socket("http://localhost/index.html");

        //local Server with Client + proxy
        //client.url_socket("http://localhost/index.html", "92.222.237.109", 8888);


    }
}
