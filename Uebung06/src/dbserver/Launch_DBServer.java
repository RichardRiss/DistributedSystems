package dbserver;

import Interface.RemoteRecord;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.TimeUnit;


public class Launch_DBServer {

    private static final int PORT = 4711;  // 0 for system chosen port

    public static void main(String args[]) {

        try {
            DataBaseImpl obj = new DataBaseImpl();
            Remote stub = UnicastRemoteObject.exportObject(obj, PORT);


            writeStubToFile("Uebung06/src/stub.obj", stub);

            System.err.println("Server ready");

            RemoteRecord recordobj = obj.getRecordRef();
            Thread tr = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            System.out.println("Server-side value is: " + recordobj.getValue());
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException | RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            tr.start();

            
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void writeStubToFile(String fileName, Remote stub) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(stub);
        out.close();
    }

}
