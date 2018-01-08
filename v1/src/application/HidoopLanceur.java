package application;

import hdfs.Machine;
import hdfs.NameNode;
import hdfs.NameNodeImpl;
import ordo.RMInterface;
import ordo.RessourceManager;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;


public class HidoopLanceur {
    /**
     * Cette classe permet de lancer Hidoop
     */


    public static void main (String[] args) {
        /* On ajoute le RessourceManager à l'annuaire */
        String[] cmdRm = {"setUp.txt"};
        RMInterface resMan = null;

        // Lancer l'annuaire
        System.out.println("Lancement de l'annuaire ...");
        try {
            Registry registry = LocateRegistry.createRegistry(1199);
            System.out.println("OK");
        } catch (RemoteException e) {
            e.printStackTrace();
        }



        /* On lance hdfs ( ce qui lance aussi les daemons*/
        // Lancer le NameNode
        String[] cmdNn = {"setUp.txt"};
        try {
            NameNodeImpl.main(cmdNn);
        } catch (RemoteException e) {
            System.out.println("Echec du lancement du NameNode");
            e.printStackTrace();
        }
        // Lancer les serveurs HDFS et les Daemons
        NameNode nn = null;
        try {
            nn = (NameNode) Naming.lookup("//localhost:1199/NameNode");
        } catch (RemoteException e) {
            System.out.println("Echec du chargement du NameNode");
            e.printStackTrace();
        }
        // On lance le RessourceManager
 catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


        List<Machine> machines;
		try {
			machines = nn.getMachines();
	        for (Machine m : machines){
	            //Lancer une machine (en local)
	            String[] port = {String.valueOf(m.getPort())};
	            new ServerRunner(port).start();
	            // Lancer les Daemons (en local)
	            String[] nomDeamon = {m.getNomDaemon()};
	            new DaemonRunner(m.getNomDaemon(), m.getPort(), m.getNom()).start();

	        }
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

       /* On lancer le RM */
        threadRM monThread = new threadRM(cmdNn);
        monThread.start();

        /* On demande à l'utilisateur s'il veut utiliser hdfs ou hidoop */
        TerminalHDFS.main(args);
    }



}

