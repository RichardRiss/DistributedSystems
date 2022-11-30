import dbclient.DBClient;
import dbserver.Launch_DBServer;

public class Main {
    public static void main(String[] args) {
        //5.2.a No because the object is instantiated locally and not referenced

        //5.2.b
        try {
            Launch_DBServer.main(args);
            DBClient.main(args);


        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}