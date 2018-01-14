package hdfs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.text.AbstractDocument.BranchElement;

import ordo.DaemonImpl;

public class NameNodeImpl extends UnicastRemoteObject implements NameNode {
	
	/*****************************************
	ATTRIBUTS
	*****************************************/	
	
	private final static int facteurDeReplication = 3;
	
	private List<Machine> machines;
	private Map<String, Fichier> fichiers; // code un fichier, la clé est son nom, la valeur est un objet Fichier
	
	/*****************************************
	CONSTRUCTEUR
	*****************************************/
	
	//Initialisation de la liste des serveurs
	public NameNodeImpl(String fichierSetup) throws RemoteException{
		// On récupère la liste des Machines
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fichierSetup));
		} catch (FileNotFoundException e) {
			System.out.println("Fichier " + fichierSetup + " introuvable");
		}
		String ligne;
		machines = new ArrayList<>();
		try {
			while ((ligne = br.readLine()) != null){
				String[] machine = ligne.split(" ");
				machines.add(new Machine(machine[1], Integer.parseInt(machine[0]), machine[2]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Liste des machines et des ports initialisées");
		
		// On initialise la liste des machines
		fichiers = new HashMap<String, Fichier>();
	}
	
	/*****************************************
	MAIN
	*****************************************/
	
	public static void main(String[] args) throws RemoteException {
		// On vérifie que l'utilisateur lance le main correctement
		if(args.length != 1){
			System.out.println("Usage : java NameNodeImple <file>");
			return;
		}
		
		//Récupérer les serveurs et les numéros de port depuis le fichier spécifié
		NameNode monNameNode = new NameNodeImpl(args[0]);
		
		// Se connecter à l'annuaire
		try {
			Naming.rebind("//localhost:1199/NameNode",  monNameNode);
		} catch (RemoteException | MalformedURLException e) {
			System.out.println("Echec de la connexion du NameNode à l'annuaire !");
			e.printStackTrace();
		}
	}

	/*****************************************
	METHODES
	*****************************************/
	
	public List<String> getFragments(String nomFichier) throws RemoteException {
		return fichiers.get(nomFichier).getFragments();
	}

	public Machine getMachineFragment(String nomFragment, List<Machine> machineInutilisables) throws RemoteException {
		List<Machine> mFrag = new ArrayList<>();

		// Récupérer la liste des machines contenant ce fragment
		// Seulement si elles ne sont pas dans la liste des machinesInutilisables
		for(Machine m : machines) {
			if(m.containsFragment(nomFragment) && ((machineInutilisables == null) || !machineInutilisables.contains(m))) {
				mFrag.add(m);
			}
		}		
		
		// Trouver la machine la moins pleine (en nombre de fragments)
		if(mFrag.isEmpty()) {
			return null;
		}
		int min = mFrag.get(0).getFragments().size();
		Machine mRes = mFrag.get(0);
		for(Machine m : mFrag) {
			if(m.getFragments().size() <= min) {
				min = m.getFragments().size();
				mRes = m;
			}
		}

		// Renvoyer le résultat
		return mRes;
	}
	
	public Machine getMachineMoinsPleine() throws RemoteException{
		Machine meilleureMachine = machines.get(0);
		for (Machine m : machines){
			if(m.getFragments().size() < meilleureMachine.getFragments().size()){
				meilleureMachine = m;
			}
		}
		return meilleureMachine;
	}


	public List<Machine> getAllMachinesFragment(String nomFragment) throws RemoteException{
		List<Machine> mFrag = new ArrayList<>();
		
		for(Machine m : machines) {
			if(m.containsFragment(nomFragment)) {
				mFrag.add(m);
			}
		}
		
		return mFrag;
	}

	public List<String> getAllFragmentFichierMachine(Machine m, String nomFichier) throws RemoteException{
		List<String> frag = new ArrayList<>();
		
		for(String f : m.getFragments()) {
			// si le fragment commence par le nom du fichier
			if(f.startsWith(nomFichier)) {
				// alors on vérifie que c'est bien un fragment, et pas un autre fichier
				String fin = f.replaceFirst(nomFichier, "");
				
				try {
					Integer.parseInt(fin);
					frag.add(f);
				} catch (NumberFormatException e) {
					// Si ça lève une exception, ce n'est pas un fragment de ce fichier
				}
			}
		}
		return frag;
	}

	public List<Machine> getMachinesFichier(String nomFichier) throws RemoteException{
		List<Machine> list = new ArrayList<>();
		
		// Pour chaque machine
		for(Machine m : machines) {
			// On vérifie si elle possède un fragment de ce fichier
			for(String f : m.getFragments()) {
				// si un fragment commence par le nom du fichier
				if(f.startsWith(nomFichier)) {
					String fin = f.replaceFirst(nomFichier, "");
					
					try {
						Integer.parseInt(fin);
						list.add(m);
						break;
					} catch (NumberFormatException e) {
						// si ça lève une exception, ce n'est pas un fragment de ce fichier
					}
				}
			}
		}
		
		return list;
	}
	
	public void ajoutFichierHdfs(String nomFichier) throws RemoteException {
		Fichier f = new Fichier(nomFichier);
		f.setNbFragments(0);
		fichiers.put(nomFichier, f);

	}
	
	public void ajoutFragmentMachine(Machine machine, String nomFichier, String nomFragment, int numeroFragment) throws RemoteException {
		for(Machine m : machines) {

			if(machine.getNom().equals(m.getNom())
			&& machine.getPort() == m.getPort()
			&& machine.getNomDaemon().equals(m.getNomDaemon())) {
				m.getFragments().add(nomFragment);
			}
		}
		Fichier f = fichiers.get(nomFichier);
		f.setNbFragments(Math.max(numeroFragment + 1, f.getNbFragments()));
	}
	
	public void supprimeFichierHdfs(String nomFichier) throws RemoteException {
		for(Machine m : machines) {
			for(int i = 0; i < m.getFragments().size(); i++) {
				String fragment = m.getFragments().get(i);
				if (fragment.startsWith(nomFichier)){
					m.getFragments().remove(fragment);
				}
			}
		}
		fichiers.remove(nomFichier);
	}


	public boolean fileExists(String fileName) throws RemoteException{
		return (fichiers.get(fileName) != null);
	}

	/*****************************************
	GETS && SETS
	*****************************************/
	public List<Machine> getMachines() {
		return machines;
	}

	public int getFacteurdereplication() {
		return facteurDeReplication;
	}
}
