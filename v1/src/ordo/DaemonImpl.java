package ordo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import formats.Format;
import map.Mapper;

public class DaemonImpl extends UnicastRemoteObject implements Daemon {

	private static final long serialVersionUID = 1L;
	
	private String name; // Les démons ont un nom pour qu'on puisse les différencier
	
	protected DaemonImpl(String name) throws RemoteException {
		super();
		this.name = name;
		System.out.println("Création du Deamon " + this.name);
	}

	@Override
    // On appelle le map fourni en paramètre sur le reader et on écrit sur le writer
	// Quand on a finit, on appelle le callback pour l'informer
	public void runMap(Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException {
		// On ouvre le formats sur le démons, pour récupérer les chunks
		reader.open(Format.OpenMode.R);
		writer.open(Format.OpenMode.W);
		
		

		System.out.println("Lancement du Map ...");
		m.map(reader, writer);
		System.out.println("OK");
		
		reader.close();
		writer.close();
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
			Daemon d = new DaemonImpl(args[0]);
			// On l'enregistre auprès du serveur de nom, qu'il faudra avoir lancé au préalable !
            String nomMachine = InetAddress.getLocalHost().getHostName();
            //Naming.rebind("//" + "localhost/" + ((DaemonImpl) d).getName(), d);
            //Registry registry = LocateRegistry.createRegistry(1099);
            //registry.rebind("//localhost:1099",  d);
            System.out.println("//localhost:1199/" + ((DaemonImpl) d).getName());
            Naming.rebind("//localhost:1199/" + ((DaemonImpl) d).getName(), d);
            System.out.println("Done !");
			
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
