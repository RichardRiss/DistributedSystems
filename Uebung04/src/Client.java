import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

public class Client {
    DBResult dbr = new DBResult();
    DataBase db = null;
    public Client(String filename) throws IOException, ClassNotFoundException {
        this.db = readStubFromFile(filename);
    }

    public int getIndex(String record) {
        dbr.setIndex(0);
        dbr.setRecord(record);
        try {
            dbr.setIndex(this.db.getIndex(dbr.getRecord()));
        } catch (RemoteException e) {
            System.out.println("Remote Exception");
            e.printStackTrace();
        }
        return dbr.getIndex();
    }

    public void addRecord(int index, String record) {
        dbr.setIndex(index);
        dbr.setRecord(record);
        try {
            this.db.addRecord(dbr.getIndex(), dbr.getRecord());
        } catch (RemoteException e) {
            System.out.println("Remote Exception");
            e.printStackTrace();
        }
    }

    public String getRecord(int index) {
        dbr.setRecord("");
        dbr.setIndex(index);
        try {
            dbr.setRecord(this.db.getRecord(dbr.getIndex()));
        } catch (RemoteException | NullPointerException e) {
            System.out.println("Remote Exception");
            e.printStackTrace();
            dbr.setRecord(null);
        }
        return dbr.getRecord();
    }

    public int getSize() throws IOException {
        dbr.setSize(0);
        try {
            dbr.setSize(this.db.getSize());
        } catch (RemoteException e) {
            System.out.println("Remote Exception");
            e.printStackTrace();
        }
        return dbr.getSize();
    }


    private static DataBase readStubFromFile(String fileName)
        throws FileNotFoundException, IOException, ClassNotFoundException {
        /* Deserialize stub using Java Serialization */
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(fis);
        DataBase remoteObj = (DataBase)in.readObject();
        in.close();
        return remoteObj;
    }

}
