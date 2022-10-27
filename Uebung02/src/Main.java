public class Main {
    public static void main(String[] args) {
        SocketClient client = new SocketClient();

        //Client with basic socket class
        client.normal_socket("www.fh-wedel.de", 80);

        //Client with URL socket class
        client.url_socket("http://www.fh-wedel.de/index.html");

        //Client with proxy
        //client.url_socket("http://www.fh-wedel.de/index.html", "92.222.237.109", 8888);


    }
}
