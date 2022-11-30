package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteRecord extends Remote {
    public String getValue () throws RemoteException;
    public void setValue ( String value ) throws RemoteException ;
}

