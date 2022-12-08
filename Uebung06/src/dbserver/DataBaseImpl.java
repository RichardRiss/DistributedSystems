package dbserver;

import Interface.DataBase;
import Interface.RemoteRecord;

import java.rmi.RemoteException;
import java.util.HashMap;

public class DataBaseImpl implements DataBase {

	private HashMap<Integer,String> m_records;
	private RemoteRecordImpl recordobj;
	
	public DataBaseImpl () throws RemoteException {
		m_records = new HashMap<>();
		recordobj = new RemoteRecordImpl();
	}
	
	public void addRecord(int index, String record) throws RemoteException {
		m_records.put(index, record);		
	}

	public String getRecord(int index) throws RemoteException {
		return m_records.get(index);
	}

	public int getSize() throws RemoteException {
		return m_records.size();
	}

	public RemoteRecord getRecordRef() throws RemoteException {
		return recordobj;
	}

  }
