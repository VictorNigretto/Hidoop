package ordo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import formats.*;
import hdfs.Machine;
import hdfs.NameNode;
import map.MapReduce;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;

import static hdfs.HdfsClient.HdfsRead;
import hdfs.NameNode;

public class Job implements JobInterface {
	
	/*****************************************
	Attributs
	*****************************************/
	
	private int numberOfReduces;
	private int numberOfMaps;
	private Format.Type inputFormat;
	private Format.Type outputFormat;
	private String inputFName;
	private String resReduceFName;
	private String outputFName;
	private SortComparator sortComparator;
	private Format.Type interFormat;
	private String interFName;
	private List<String> machines; //la liste des machines sur lesquelles tournent les démons


	/*****************************************
	Constructeurs
	*****************************************/

	// Constructeur vide avec les données minimums
	// Le reste à étant à remplir par l'utilisateur
	public Job() {
		//Initialisation des machines
		this.numberOfMaps = 0;
		this.numberOfReduces = 1; //Pour la V0 uniquement
		this.sortComparator = new SortComparatorLexico(); //TODO
	}

	// On peut aussi ajouter directement l'input
	// L'output étant à remplir par l'utilisateur
	public Job(Format.Type inputFormat, String inputFName) {
		this.machines = new ArrayList<String>();
		this.inputFormat = inputFormat;
		this.setInputFname(inputFName);
		this.outputFormat = inputFormat;
		this.interFormat = inputFormat;
	}

	/*****************************************
	Start Job (méthode principale)
	*****************************************/
	
    public void startJob (MapReduce mr) {
    	// 0) déterminer où lancer les maps
        // 1) lancer les maps sur tous les chunks du fichier
        // 2) les récupérer quand ils ont finis
        // 3) les concatener dans le fichier résultat avec le reduce qui s'exécutera sur tous les résultats des maps


    	System.out.println("Lancement du job ...");
    	
    	
		// Créons le format d'input, intermédiaire et d'output pour le client et tous les démons
		Format input, inter, resReduce, output;
        if(inputFormat == Format.Type.LINE) { // LINE
			input = new FormatLine(inputFName);
		} else { // KV
			input = new FormatKV(inputFName);
		}
		inter = new FormatKV(interFName);
		resReduce = new FormatKV(resReduceFName);
		output = new FormatKV(outputFName);

		
		
		// On récupere les noms des démons à patrir du ressourceManager
		RMInterface ResMan = null;
		try {
			ResMan = (RMInterface) Naming.lookup("//localhost:1199/RessourceManager");
			ResMan.ajouterFichier(inputFName);
			machines = ResMan.RecupererNomDemons(inputFName);
			System.out.println(machines);
			numberOfMaps = machines.size();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		/*try {
			for(Machine m : ResMan.getMachines()) {
				DaemonImpl.DemonsLances.release();
			}
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		// récupérer la liste des démons sur l'annuaire
		System.out.println("Récupération de la liste des Daemons ...");
		List<Daemon> demons = initDemons(0);
    	System.out.println("OK\n");

    	
    	// On initialise le callback pour que les démons puissent renvoyer leurs résultats
		CallBack cb = null;
		try {
			cb = new CallBackImpl();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		
		// Puis on va lancer les maps sur les différents démons
		System.out.println("Lancement des Maps ...");

		// enregistrement sur le nameNode du fichier intermédiaire
		NameNode nn = null;
		try {
			nn = (NameNode) Naming.lookup("//localhost:1199/NameNode");
			nn.ajoutFichierHdfs(inter.getFname());
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		for(int i = 0; i < this.numberOfMaps; i++) {
			Daemon d = demons.get(i);
			
			// On change le nom des Formats en rajoutant un numéro pour que les fragments aient des noms différents pour chaque Daemon
			Format inputTmp;
	        if(inputFormat == Format.Type.LINE) { // LINE
	        	inputTmp = new FormatLine(input.getFname() + "" + i);
			} else { // KV
	        	inputTmp = new FormatKV(input.getFname() + "" + i);
			}
	        Format interTmp = new FormatKV(inter.getFname() + "" + i);
	        
			// on appelle le map sur le démon
			MapRunner mapRunner = new MapRunner(d, mr, inputTmp, interTmp, cb);
			mapRunner.start();

			//On prévient le NameNode qu'on a ajouté un fragment à la machine
			try {

				nn.ajoutFragmentMachine(((Daemon) d).getMachine(), inter.getFname(), inter.getFname() +"" + i, i);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
    	System.out.println("OK\n");

    	
		// Puis on attends que tous les démons aient finis leur travail
    	System.out.println("Attente de la confirmation des Daemons ...");
		try {
			cb.waitFinishedMap(numberOfMaps);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	System.out.println("OK\n");

		//Suppression du fichier du ressourceManager
		try {
			System.out.println(ResMan);
			System.out.println(inputFName);
			ResMan.enleverFichier(inputFName);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		// On utilise HDFS pour récupérer le fichier résultat concaténé dans resReduce
    	System.out.println("Récupération du fichier résultat ...");
		HdfsRead(inter.getFname(), resReduce.getFname());
    	System.out.println("OK\n");
		
    	
		// On ferme notre fichier avant de le réouvrir
		resReduce.close();
		resReduce.open(Format.OpenMode.R);

    	// On veut transformer ce fichier en un format local
        output.open(Format.OpenMode.W);

		// Puis on applique le reduce sur le résultat concaténé des maps
		// On stock le résultat dans l'output
    	System.out.println("Lancement du Reduce ...");
    	mr.reduce(resReduce, output);
    	output.close();
    	System.out.println("OK\n");
    	
    	
    	System.out.println("Fin du job, merci pour votre patience :)");
	}
	
	/*****************************************
	Méthodes auxiliares
	*****************************************/


    public List<Daemon>  initDemons(int debut) {
    	List<Daemon> demons = new ArrayList<>();
    	for(int i = debut; i < this.numberOfMaps; i++) {
    		try {
    		    // On va récupérer les Démons en RMI sur un annuaire
				// TODO => généraliser à plusieurs démons sur plusieurs machines
    			System.out.println("On se connecte à : " + "//localhost:1199/" + machines.get(i));
				demons.add((Daemon) Naming.lookup("//localhost:1199/" + machines.get(i)));
				//demons.add((Daemon) Naming.lookup("//localhost/premierDaemon"));
    		} catch (RemoteException | NotBoundException e) {
    			// Dans ce cas on essaye de changer de Daemon
    			System.out.println("Veuillez patienter un moment, nous essayons un autre Daemon");
    			try {
    				RMInterface RM = (RMInterface) Naming.lookup("//localhost:1199/RessourceManager");
    				String nomDemon = RM.RecupererDemonFragment(inputFName);
    				
    				if (nomDemon == null) {
    					System.out.println("Il n'y a plus de démons fonctionnels pouvant effectuer le map sur ce fragment de fichier");
    				}else {
    					machines.set(i, nomDemon);  				}
    					initDemons(i);
    			} catch (NotBoundException e1) {
    				e.printStackTrace();
    			} catch (MalformedURLException e1) {
    				e1.printStackTrace();
    			} catch (RemoteException e1) {
    				System.out.println("Meme le RessourceManager est mort, nous ne pouvons plus rien faire, veuillez nous excuser");
    			}
    		} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return demons;
    }
    
    public void setNumberOfReduces(int tasks){
    	this.numberOfReduces = tasks;
    }
    
    public void setNumberOfMaps(int tasks) {
    	this.numberOfMaps = tasks;
    }
    
    public void setInputFormat(Format.Type ft){
    	this.inputFormat = ft;
		this.outputFormat = inputFormat;
		this.interFormat = inputFormat;
    }
    
    public void setOutputFormat(Format.Type ft){
    	this.outputFormat = ft;
    }
    
    public void setInputFname(String fname){
    	this.inputFName = fname;
    	this.outputFName = fname + "-final";
		this.interFName = fname + "-inter";
		this.resReduceFName = fname + "-res";
    }
    
    public void setOutputFname(String fname){
    	this.outputFName = fname;
    }
    
    public void setSortComparator(SortComparator sc){
    	this.sortComparator = sc;
    }
    
    public int getNumberOfReduces(){
    	return this.numberOfReduces;
    }
    
    public int getNumberOfMaps() {
    	return this.numberOfMaps;
    }
    
    public Format.Type getInputFormat(){
    	return this.inputFormat;
    	
    }
    
    public Format.Type getOutputFormat(){
    	return this.outputFormat;
    }
    
    public String getInputFname(){
    	return this.inputFName;
    	
    }
    
    public String getOutputFname(){
    	return this.outputFName;
    }
    
    public SortComparator getSortComparator(){
    	return this.getSortComparator();
    }


}
