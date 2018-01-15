package ordo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;

import formats.Format;
import hdfs.Machine;
import hdfs.NameNode;
import map.Mapper;

public class DaemonImpl extends UnicastRemoteObject implements Daemon {

	private static final long serialVersionUID = 1L;
	
	static private String name; // Les démons ont un nom pour qu'on puisse les différencier
	private Machine machine;
	public static  Semaphore RMlance = new Semaphore(0);
	public static Semaphore DemonsLances = new Semaphore(0);
	private static Semaphore protectName = new Semaphore(1);

	
	public DaemonImpl(String nomDaemon, int port, String name ) throws RemoteException {
		super();
		this.name = nomDaemon;
		this.machine = new Machine(name, port, nomDaemon);
		System.out.println("Création du Deamon " + this.name);
		protectName.release();
        //try {
        	//TODO il faut le garder mais probleme de compatibilité avec le namenode ( pour lui, tous les noms de machine sont des localhost)
			//machine.setNom(InetAddress.getLocalHost().getHostName());
		//} catch (UnknownHostException e) {
		//	e.printStackTrace();
		//}

	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;

	}

	public Semaphore getRMlance() {
		return RMlance;
	}

	public void setRMlance(Semaphore rMlance) {
		RMlance = rMlance;
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
			protectName.acquire();
			Daemon d = new DaemonImpl(args[0], Integer.parseInt(args[1]), args[2]);
			// On l'enregistre auprès du serveur de nom, qu'il faudra avoir lancé au préalable !
            //Naming.rebind("//" + "localhost/" + ((DaemonImpl) d).getName(), d);
            //Registry registry = LocateRegistry.createRegistry(1099);
            //registry.rebind("//localhost:1099",  d);
            System.out.println("//localhost:1199/" + ((DaemonImpl) d).getName());
            Naming.rebind("//localhost:1199/" + ((DaemonImpl) d).getName(), d);
            System.out.println("Done !");
            RMlance.acquire();

            RMInterface rm = ((RMInterface) Naming.lookup("//localhost:1199/RessourceManager"));
            //DemonsLances.release();
            while (true) {
            	rm.DemonFonctionne(name);
            }
			
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
