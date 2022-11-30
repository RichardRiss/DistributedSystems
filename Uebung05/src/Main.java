import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            DataBaseImpl db = new DataBaseImpl("./Uebung05/src/database");
            Client client = new Client(db.path);

            client.addRecord(4101, "Appen");
            client.addRecord(4102, "Ahrensburg");
            client.addRecord(4103, "Wedel");
            client.addRecord(4104, "Aumühle");
            client.addRecord(4105, "Seevetal");
            client.addRecord(4106, "Quickborn");

            String record4103 =  client.getRecord(4103);
            String record4107 = client.getRecord(4107);
            int size = client.getSize();
            int indexAppen = client.getIndex("Appen");
            int indexasd = client.getIndex("asd");
            DBResult obj = client.getRecordObj(4101);


            System.out.println("Key 4103 has record: " + record4103);
            System.out.println("Key 4107 has record: " + record4107);
            System.out.println("Final size is " + size);
            System.out.println("Appen has Index " + indexAppen);
            System.out.println("asd has Index " + indexasd);
            System.out.println("Object holds record " + obj.getRecord());
            System.out.println("Object holds index " + obj.getIndex());



        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found Exception");
            e.printStackTrace();
        }
    }


}