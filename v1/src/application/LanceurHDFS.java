package application;

import hdfs.HdfsClient;
import hdfs.HdfsServer;
import hdfs.Machine;
import hdfs.NameNode;
import hdfs.NameNodeImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class LanceurHDFS {

// Une ébauche pour pouvoir lancer les différentes applications
// Mais nous n'avons pas réussi à la faire fonctionner ...

    public static void main(String[] args) {
    	// Lancer l'annuaire
        System.out.println("Lancement de l'annuaire ...");
        try {
            Registry registry = LocateRegistry.createRegistry(1199);
            System.out.println("OK");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    	// Lancer le NameNode
    	String[] cmdNn = {"setUp.txt"};
    	try {
			NameNodeImpl.main(cmdNn);
		} catch (RemoteException e) {
			System.out.println("Echec du lancement du NameNode");
			e.printStackTrace();
		}
    	// Lancer les serveurs HDFS et les Daemons
    	NameNodeImpl nn = null;
		try {
			nn = new NameNodeImpl(cmdNn[0]);
		} catch (RemoteException e) {
			System.out.println("Echec du chargement du NameNode");
			e.printStackTrace();
		}
    	List<Machine> machines = nn.getMachines();
    	for (Machine m : machines){
    		//Lancer une machine (en local)
    		String[] port = {String.valueOf(m.getPort())};
    		new ServerRunner(port).start();
        	// Lancer les Daemons (en local)
    		String[] nomDeamon = {m.getNomDaemon()};
            new DaemonRunner(nomDeamon).start();

    	}
    }

}














