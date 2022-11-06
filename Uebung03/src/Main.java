public class Main {
    public static void main(String[] args) {
        SocketServer server = new SocketServer();
        SocketClient client = new SocketClient("http://localhost/");

        client.addRecord(4101, "Appen");
        client.addRecord(4102, "Ahrensburg");
        client.addRecord(4103, "Wedel");
        client.addRecord(4104, "Aumühle");
        client.addRecord(4105, "Seevetal");
        client.addRecord(4106, "Quickborn");

        String record4103 =  client.getRecord(4103);
        String record4107 = client.getRecord(4107);
        int size = client.getSize();

        System.out.println("Key 4103 has record: " + record4103);
        System.out.println("Key 4107 has record: " + record4107);
        System.out.println("Final size is " + size);



    }
}