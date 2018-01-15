package ordo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Semaphore;

import application.DaemonRunner;
import application.ServerRunner;
import hdfs.Fichier;
import hdfs.Machine;
import hdfs.NameNode;


public class RessourceManager extends UnicastRemoteObject implements RMInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*****************************************
	ATTRIBUTS
	*****************************************/	
	
	private List<String> fragments; // liste des fragments de chaques fichiers reconnus par le RessourceManager
	private Map<String,List<String>> demonsDuFragment;//la clef est un nom de fragment et la valeur est la liste des daemons associée au fragment
	private Map<String,Boolean> demonsFonctionnent;
	private NameNode notreNameNode;
	private Collection<Machine> machines;
	private Map<String, Integer> quantiteJob; // le clef est le nom du démon et la valeure est le nombre de jobs utilisant ce démon
	private Map<String, String> nomMachines; // la clef est le nom du démon, la valeure est le nom de la machine associée
	/*****************************************
	CONSTRUCTEUR
	*****************************************/
	
	//Initialisation de la liste des serveurs
	public RessourceManager(String fichierSetup) throws RemoteException{
		// On récupère la liste des Machines
		this.fragments = new ArrayList<String>();
		this.demonsFonctionnent = new HashMap<String, Boolean>();
		this.demonsDuFragment = new HashMap<String, List<String>>();
		this.quantiteJob = new HashMap<String, Integer>();
		this.nomMachines = new HashMap<String,String>();
		
		// on initialise la liste des demons
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fichierSetup));
		} catch (FileNotFoundException e) {
			System.out.println("Fichier " + fichierSetup + " introuvable");
		}
		String ligne;
		machines = new ArrayList<Machine>();
		Machine m;
		try {
			while ((ligne = br.readLine()) != null){
				String[] demon = ligne.split(" ");
				m = new Machine(demon[1], Integer.parseInt(demon[0]), demon[2]);
				machines.add(m);
				nomMachines.put(m.getNomDaemon(), m.getNom());
				quantiteJob.put(demon[2],0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Liste des démons initialisée");
		}
	
	
	/*****************************************
	MAIN
	 * @throws NotBoundException 
	 * @throws MalformedURLException 
	*****************************************/

	public static void main(String[] args) {
		
		RMInterface ResMan;
		try {
			ResMan = new RessourceManager(args[0]); // args[0] est le fichier setUp.txt
				
			Naming.rebind("//localhost:1199/RessourceManager", ResMan);
		// On se connecte au NameNode
		
			ResMan.setNotreNameNode((NameNode) Naming.lookup("//localhost:1199/NameNode"));


		List<Machine> machines =null;
	
			machines = ResMan.getNotreNameNode().getMachines();

		for (Machine m : machines){
			DaemonImpl.RMlance.release();
        }
		
		// Boucle while appelant les demons pour confirmer leur etat et met a jour la liste des demons si un ne fonctionne plus
		while (true) {
			List<String> rm = new ArrayList<String>();
			String nomD;
			// TODO le diviser en plusieurs threads
			for (Machine m : ResMan.getMachines()) {
				nomD = m.getNomDaemon();
				ResMan.getDemonsFonctionnent().put((nomD), false);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (ResMan.getDemonsFonctionnent().get(nomD) == false) {
					rm.add(nomD);
				}
				
			}
			for (String demon : rm){
				ResMan.supprimeDemon(demon);
			}
			
			}
		} catch (RemoteException | MalformedURLException | NotBoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	/*****************************************
	METHODES
	*****************************************/

	public void setFragments(List<String> fragments) {
		this.fragments = fragments;
	}

	public Map<String, Integer> getQuantiteJob() {
		return quantiteJob;
	}

	public void setQuantiteJob(Map<String, Integer> quantiteJob) {
		this.quantiteJob = quantiteJob;
	}


	public void DemonFonctionne(String nomDemon) {
		demonsFonctionnent.put(nomDemon, true);
	}
	
	public void supprimeDemon(String nomDemon) {
		List<Machine> anciennesMachines = new ArrayList<Machine>();
		
		//Suppression du démon dans demonsFonctionnent
		demonsFonctionnent.remove(nomDemon);
		
		//Suppression du démon dans machines (on supprime toutes les machines qui ont ce demon en attribut)
		for (Machine m : machines) {
			if (m.getNomDaemon() == nomDemon) {
				anciennesMachines.add(m);
			}
		}
		for(Machine m : anciennesMachines){
			machines.remove(m);
		}
		
		//Suppression du démon dans demonsDuFragment
		for (String f : fragments) {
			demonsDuFragment.get(f).remove(nomDemon);
		}
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

	public void setMachines(Collection<Machine> machines) {
		this.machines = machines;
	}

	public Map<String, List<String>> getDemonsDuFragment() {
		return demonsDuFragment;
	}

	public NameNode getNotreNameNode() {
		return notreNameNode;
	}

	public Collection<Machine> getMachines() {
		return machines;
	}

	public void setDemonsFonctionnent(Map<String, Boolean> demonsFonctionnent) {
		this.demonsFonctionnent = demonsFonctionnent;
	}




	public List<String> getFragments() {
		return fragments;
	}
	//méthode donnant un Demon contenant un fragment de fichier

	public void ajouterFichier(String Fname) {
		List<Machine> machinesAdd;
		List<String> fragmentsRecup;

		try {
			machinesAdd = notreNameNode.getMachinesFichier(Fname);
			System.out.println(machines);
			System.out.println(Fname);
			for (Machine m : machinesAdd) {
				// mise à jour de demons
				machines.add(m);
				fragmentsRecup = notreNameNode.getAllFragmentFichierMachine(m, Fname);
				for (String frag : fragmentsRecup) {
					// mise à jour de fragments
					if (!fragments.contains(frag)) {
						this.fragments.add(frag);
					}
					// mise à jour de demonsDuFragment
					if (demonsDuFragment.containsKey(frag)) {
						demonsDuFragment.get(frag).add(m.getNomDaemon());
					}else{
						List<String> listD =  new ArrayList<String>();
						listD.add(m.getNomDaemon());
						demonsDuFragment.put(frag,listD);
					}
				}


			}
			System.out.println(demonsDuFragment);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	// Renvoie une liste de démons qui fonctionnent correspondant chacun à un fragment du fichier de nom Fname
	public ArrayList<String> RecupererNomDemons(String Fname) {
		int min;
		int i;
		int i_min;
		ArrayList<String> res = new ArrayList<String>();
		for (String frag : fragments) {
			if (frag.startsWith(Fname)) {
				min =0;
				i = 0;
				i_min = 0;
				for (String demon : demonsDuFragment.get(frag)) {
					if (quantiteJob.get(demon) <= min) {
						min = quantiteJob.get(demon);
						i_min = i;
					}
					i++;
				}
				res.add((demonsDuFragment.get(frag)).get(i_min) );
				quantiteJob.put(demonsDuFragment.get(frag).get(i_min), min + 1);
			}
		}
		System.out.println("listeDesNoms = " + res);
		return res;
	}

	public String RecupererDemonFragment(String Fname) {
		String res = null;
		for (String frag : fragments) {
			if (frag.equals(Fname)) {
				res = (demonsDuFragment.get(frag)).get(0);
			}
		}
		return res;

	}

	public void enleverFichier(String Fname) {
		List<String> fragmentsSupprimes = new ArrayList<String>();
		for (String f : fragments){
			if (f.startsWith(Fname)) {
				for (String demon : demonsDuFragment.get(f)) {
					quantiteJob.put(demon, quantiteJob.get(demon) - 1);
				}
			}
			fragmentsSupprimes.add(f);
		}
		for (String f : fragmentsSupprimes) {
			fragments.remove(f);
		}
	}



}

