package ordo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import formats.Format;
import hdfs.Machine;
import map.Mapper;

public class DaemonImpl extends UnicastRemoteObject implements Daemon {

	private static final long serialVersionUID = 1L;
	
	private String name; // Les démons ont un nom pour qu'on puisse les différencier
	private Machine machine;
	
	
	protected DaemonImpl(String nomDaemon, int port, String name ) throws RemoteException {
		super();
		this.name = nomDaemon;
		this.machine = new Machine(name, port, nomDaemon);
		System.out.println("Création du Deamon " + this.name);
		
        try {
        	
			machine.setNom(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
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
			
			Daemon d = new DaemonImpl(args[0], Integer.parseInt(args[1]), args[2]);
			// On l'enregistre auprès du serveur de nom, qu'il faudra avoir lancé au préalable !
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
