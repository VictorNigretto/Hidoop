package ordo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import formats.Format;
import map.Mapper;

public class DaemonImpl extends UnicastRemoteObject implements Daemon {

	private static final long serialVersionUID = 1L;
	
	protected DaemonImpl() throws RemoteException {
		super();
	}

	@Override
	public void runMap(Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException {		
	}
}
