package ordo;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import formats.Format;
import map.Mapper;

public class DaemonImpl extends UnicastRemoteObject implements Daemon {

	private static final long serialVersionUID = 1L;
	
	private String name; // Les démons ont un nom pour qu'on puisse les différencier
	
	protected DaemonImpl(String name) throws RemoteException {
		super();
		this.name = name;
	}

	@Override
    // On appelle le map fourni en paramètre sur le reader et on écrit sur le writer
	// Quand on a finit, on appelle le callback pour l'informer
	public void runMap(Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException {
		m.map(reader, writer);
		try {
			cb.confirmFinishedMap();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	// Pour lancer un démon sur sa machine !
	// Le premier paramètre sera le nom du démon
	public static void main(String args[]) {
		try {
			// On le fait s'auto-construire ! Le pauvre XD Il est pas né qu'il faut déjà qu'il travail :D
			Daemon d = new DaemonImpl(args[0]);
			
			// On l'enregistre auprès du serveur de nom, qu'il faudra avoir lancé au préalable !
			Naming.rebind("//localhost/" + ((DaemonImpl) d).getName(), d);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
