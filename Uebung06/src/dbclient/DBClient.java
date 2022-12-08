package dbclient;

import Interface.DataBase;
import Interface.RemoteRecord;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeUnit;

public class DBClient {

    public static void main(String[] args) throws Exception {
        DataBase db = readStubFromFile("Uebung06/src/stub.obj");

        db.addRecord(4101, "Appen");
        db.addRecord(4102, "Ahrensburg");
        db.addRecord(4103, "Wedel");
        db.addRecord(4104, "Aumühle");
        db.addRecord(4105, "Seevetal");
        db.addRecord(4106, "Quickborn");
        RemoteRecord recobj = db.getRecordRef();

        System.out.println("size: " + db.getSize());
        System.out.println("4103: " + db.getRecord(4103));
        System.out.println("4107: " + db.getRecord(4107));
        TimeUnit.SECONDS.sleep(3);
        System.out.println("Invoking set value on record object.");
        recobj.setValue("scotty beam me up");

    }

    private static DataBase readStubFromFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        /* Deserialize stub using Java Serialization */
          FileInputStream fis = new FileInputStream(fileName);
          ObjectInputStream in = new ObjectInputStream(fis);
          DataBase remoteObj = (DataBase)in.readObject();
          in.close();
        return remoteObj;
    }

    private static RemoteRecord readObjFromFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        /* Deserialize stub using Java Serialization */
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(fis);
        RemoteRecord remoteObj = (RemoteRecord)in.readObject();
        in.close();
        return remoteObj;
    }
}
