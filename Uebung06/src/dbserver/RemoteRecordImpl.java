package dbserver;

import Interface.RemoteRecord;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteRecordImpl implements RemoteRecord {

    public RemoteRecordImpl () throws RemoteException {
        UnicastRemoteObject.exportObject (this , 4711);
    }
    private String value ;
    public String getValue () throws RemoteException {
        return value ;
    }
    public void setValue ( String value ) throws RemoteException {
        this. value = value ;
    }
}
