import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataBaseImpl implements DataBase{
    Remote remote = null;
    String path = "";
    Map<Integer, String> db = new HashMap<>();

    DBResult dbr = new DBResult();

    public DataBaseImpl(String local_path) throws RemoteException {
        this.remote = UnicastRemoteObject.exportObject(this,80);
        path = Path.of(local_path).toAbsolutePath().toString();
        try {
            writeStubToFile(path ,this.remote);
        } catch (IOException e) {
            System.out.println("File not written. Error!");
            e.printStackTrace();
        }
        System.out.println("Remote object written to " + path);
    }

    @Override
    public DBResult getRecordObj(int index) {
        dbr.setIndex(index);
        dbr.setRecord("");
        if (this.db.containsKey(dbr.getIndex())) {
            dbr.setRecord(this.db.get(dbr.getIndex()));
        }
        return dbr;

    }

    @Override
    public String getRecord(int index) throws RemoteException {
        //reads a database record from the database given a key
        dbr.setIndex(index);
        dbr.setRecord("");

        if (this.db.containsKey(dbr.getIndex())) {
            dbr.setRecord(this.db.get(dbr.getIndex()));
        }
        return dbr.getRecord();
    }

    @Override
    public int getIndex(String record) throws RemoteException {
        dbr.setIndex(0);
        for (Map.Entry<Integer, String> entry: this.db.entrySet()) {
            if (Objects.equals(record, entry.getValue())) {
                dbr.setIndex(entry.getKey());
            }
        }
        return dbr.getIndex();
    }
    @Override
    public void addRecord(int index, String record) throws RemoteException {
        //writes a database record to the database using a given key
        dbr.setIndex(index);
        dbr.setRecord(record);
        this.db.put(dbr.getIndex(), dbr.getRecord());
        System.out.println("Entry " + index + " : " + record + " added to database.");
    }

    @Override
    public int getSize() throws RemoteException {
        //determines, how many records are stored in the database.
        dbr.setSize(db.size());
        return dbr.getSize();
    }

    private static void writeStubToFile(String fileName, Remote stub)
            throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(stub);
        out.close();
    }
}
