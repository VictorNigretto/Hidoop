package ordo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hdfs.Fichier;
import hdfs.Machine;


public class RessourceManager extends UnicastRemoteObject implements RMInterface {

	/*****************************************
	ATTRIBUTS
	*****************************************/	
	
	private Map<String, Fichier> fichiers; // code un fichier, la clé est son nom, la valeur est un objet Fichier
	private List<Daemon> demons;
	private Map<String,Boolean> demonsFonctionnent;



	/*****************************************
	CONSTRUCTEUR
	*****************************************/
	
	//Initialisation de la liste des serveurs
	public RessourceManager(String fichierSetup) throws RemoteException{
		// On récupère la liste des Machines
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fichierSetup));
		} catch (FileNotFoundException e) {
			System.out.println("Fichier " + fichierSetup + " introuvable");
		}
		String ligne;
		demons = new ArrayList<>();
		demonsFonctionnent = new HashMap<>();
		int i = 0;
		try {
			while ((ligne = br.readLine()) != null){
				System.out.println("ajout du démon " + i);

				String[] demon = ligne.split(" ");
				demons.add(new DaemonImpl(demon[2], Integer.parseInt(demon[0]), demon[1]));

				demonsFonctionnent.put(demon[2], true);
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Liste des démons initialisée");
		
		// On initialise la liste des machines
		fichiers = new HashMap<String, Fichier>();
	}
	
	
	/*****************************************
	MAIN
	*****************************************/
	public static RMInterface lancerRM(String[] args) throws RemoteException {
		if(args.length != 1){
			System.out.println("Usage : java RessourceManager <file>");
			return null;
		}
		//Récupérer les serveurs et les numéros de port depuis le fichier spécifié
		RMInterface ResMan = new RessourceManager(args[0]);

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
			for (Daemon d : ResMan.getDemons()) {
				ResMan.getDemonsFonctionnent().put(((DaemonImpl) d).getName(), false);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (ResMan.getDemonsFonctionnent().get(((DaemonImpl) d).getName()) == false) {
					ResMan.supprimeDemon(((DaemonImpl) d).getName());
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
		demons.remove(nomDemon);
	}

	public List<Daemon> getDemons() {
		return demons;
	}

	public Map<String, Boolean> getDemonsFonctionnent() {
		return demonsFonctionnent;
	}



	public void setFichiers(Map<String, Fichier> fichiers) {
		this.fichiers = fichiers;
	}

	public void setDemons(List<Daemon> demons) {
		this.demons = demons;
	}

	public void setDemonsFonctionnent(Map<String, Boolean> demonsFonctionnent) {
		this.demonsFonctionnent = demonsFonctionnent;
	}




	public Map<String, Fichier> getFichiers() {
		return fichiers;
	}
	//méthode donnant un Demon contenant un fragment de fichier
}
