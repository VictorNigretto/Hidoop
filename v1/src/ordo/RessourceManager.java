package ordo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import hdfs.Fichier;
import hdfs.Machine;
import hdfs.NameNode;


public class RessourceManager extends UnicastRemoteObject implements RMInterface {

	/*****************************************
	ATTRIBUTS
	*****************************************/	
	
	private List<String> fragments; // liste des fragments de chaques fichiers reconnus par le RessourceManager
	private Map<String,List<String>> demonsDuFragment;//la clef est un nom de fragment et la valeur est la liste des daemons associée au fragment
	private Map<String,Boolean> demonsFonctionnent;
	private NameNode notreNameNode;
	private Collection<String> demons;

	/*****************************************
	CONSTRUCTEUR
	*****************************************/
	
	//Initialisation de la liste des serveurs
	public RessourceManager(NameNode nn, String fichierSetup) throws RemoteException{
		// On récupère la liste des Machines
		this.notreNameNode = nn;
		this.fragments = new ArrayList<String>();
		this.demonsFonctionnent = new HashMap<String, Boolean>();
		this.demonsDuFragment = new HashMap<String, List<String>>();

		// on initialise la liste des demons
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fichierSetup));
		} catch (FileNotFoundException e) {
			System.out.println("Fichier " + fichierSetup + " introuvable");
		}
		String ligne;
		demons = new ArrayList<String>();
		try {
			while ((ligne = br.readLine()) != null){
				String[] demon = ligne.split(" ");
				demons.add(demon[2]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Liste des démons initialisée");
		}
	
	
	/*****************************************
	MAIN
	*****************************************/
	public static RMInterface lancerRM(NameNode nn, String fichierSetUp) throws RemoteException {


		RMInterface ResMan = new RessourceManager(nn, fichierSetUp);

		// Se connecter à l'annuaire
		try {
			Naming.rebind("//localhost:1199/RessourceManager",  ResMan);
		} catch (RemoteException | MalformedURLException e) {
			System.out.println("Echec de la connexion du RessourceManager à l'annuaire !");
			e.printStackTrace();
		}
		return ResMan;
	}
	public static void main(RMInterface ResMan) throws RemoteException {
		// On vérifie que l'utilisateur lance le main correctement

		
		//Récupérer les serveurs et les numéros de port depuis le fichier spécifié
		//RessourceManager ResMan = new RessourceManager(args[0]);
		
		// Se connecter à l'annuaire
		//try {
		//	Naming.rebind("//localhost:1199/RessourceManager",  ResMan);
		//} catch (RemoteException | MalformedURLException e) {
		//	System.out.println("Echec de la connexion du RessourceManager à l'annuaire !");
		//	e.printStackTrace();
		//}
		
		// Boucle while appelant les demons pour confirmer leur etat et met a jour la liste des demons si un ne fonctionne plus
		while (true) {
			// TODO le diviser en plusieurs threads
			for (String nomD : ResMan.getDemons()) {
				ResMan.getDemonsFonctionnent().put((nomD), false);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (ResMan.getDemonsFonctionnent().get(nomD) == false) {
					ResMan.supprimeDemon(nomD);
				}
			}
		}
	}
	
	
	/*****************************************
	METHODES
	*****************************************/
	
	public void DemonFonctionne(String nomDemon) {
		demonsFonctionnent.put(nomDemon, true);
	}
	
	public void supprimeDemon(String nomDemon) {
		demonsFonctionnent.remove(nomDemon);
	}



	public Map<String, Boolean> getDemonsFonctionnent() {
		return demonsFonctionnent;
	}



	public void setFichiers(List<String> fragments) {
		this.fragments = fragments;
	}

	public void setDemonsDuFragment(Map<String, List<String>> demonsDuFragment) {
		this.demonsDuFragment = demonsDuFragment;
	}

	public void setNotreNameNode(NameNode notreNameNode) {
		this.notreNameNode = notreNameNode;
	}

	public void setDemons(Collection<String> demons) {
		this.demons = demons;
	}

	public Map<String, List<String>> getDemonsDuFragment() {
		return demonsDuFragment;
	}

	public NameNode getNotreNameNode() {
		return notreNameNode;
	}

	public Collection<String> getDemons() {
		return demons;
	}

	public void setDemonsFonctionnent(Map<String, Boolean> demonsFonctionnent) {
		this.demonsFonctionnent = demonsFonctionnent;
	}




	public List<String> getFragments() {
		return fragments;
	}
	//méthode donnant un Demon contenant un fragment de fichier

	public void ajouterFichier(String Fname) {
		List<Machine> machines;
		List<String> fragments;

		try {
			machines = notreNameNode.getMachinesFichier(Fname);
			for (Machine m : machines) {
				// mise à jour de demons
				demons.add(m.getNomDaemon());
				fragments = notreNameNode.getAllFragmentFichierMachine(m, Fname);
				for (String frag : fragments) {
					// mise à jour de fragments
					fragments.add(frag);
					// mise à jour de demonsDuFragment
					if (demonsDuFragment.containsKey(frag)) {
						demonsDuFragment.get(frag).add(m.getNomDaemon());
					}else{
						List listD =  new ArrayList<Daemon>();
						listD.add(m.getNomDaemon());
						demonsDuFragment.put(frag,listD);
					}
				}


			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// Renvoie une liste de démons qui fonctionnent correspondant chacun à un fragment du fichier de nom Fname
	public ArrayList<String> RecupererNomDemons(String Fname) {
		ArrayList<String> res = new ArrayList<String>();
		for (String frag : fragments) {
			if (frag.startsWith(Fname)) {
				res.add((demonsDuFragment.get(frag)).get(0));
			}
		}
		return res;
	}


}

