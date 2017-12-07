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
import hdfs.NameNodeImpl;
import map.MapReduce;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import config.SetUp;

import static hdfs.HdfsClient.HdfsRead;


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
	private List<Machine> machines; //la liste des machines sur lesquelles tournent les démons
	private List<String> nomsDaemons;
	private NameNode nn;
	private HashMap<String,Machine> demonsToMachines = new HashMap<String,Machine>();

	/*****************************************
	Constructeurs
	*****************************************/

	// Constructeur vide avec les données minimums
	// Le reste à étant à remplir par l'utilisateur
	public Job() {
		this.initMachinesDaemons();
		this.numberOfMaps = machines.size();
		this.numberOfReduces = 1; //Pour la V0 uniquement
		this.sortComparator = new SortComparatorLexico(); //TODO
	}

	// On peut aussi ajouter directement l'input
	// L'output étant à remplir par l'utilisateur
	public Job(Format.Type inputFormat, String inputFName) {
		this();
		this.inputFormat = inputFormat;
		this.inputFName = inputFName;

		this.outputFName = inputFName + "-final";
		this.interFName = inputFName + "-inter";
		this.resReduceFName = inputFName + "-res";
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
    	boolean mapsfinis = false;
    	
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

		
		
		
    	// récupérer la liste des démons sur l'annuaire
    	List<Daemon> demons = new ArrayList<>();
    	for(int i = 0; i < this.numberOfMaps; i++) {
    		try {
    		    // On va récupérer les Démons en RMI sur un annuaire
				// TODO => généraliser à plusieurs démons sur plusieurs machines
    			System.out.println("On se connecte à : " + machines.get(i) + "/" + nomsDaemons.get(i));
				demons.add((Daemon) Naming.lookup(machines.get(i).getNom() +"/"+ nomsDaemons.get(i)));
				//demons.add((Daemon) Naming.lookup("//localhost/premierDaemon"));
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	System.out.println("OK\n");
    	
    	do {
    		// On initialise le callback pour que les démons puissent renvoyer leurs résultats
    		CallBack cb = null;
    		try {
    			cb = new CallBackImpl();
    		} catch (RemoteException e) {
    			e.printStackTrace();
    		}

    		nn.ajoutFichierHdfs(inter.getFname());
    		// Puis on va lancer les maps sur les différents démons
    		System.out.println("Lancement des Maps ...");
    		for(int i = 0; i < demons.size(); i++) {
    			Daemon d = demons.get(i);
			
    			// On change le nom des Formats en rajoutant un numéro pour que les fragments aient des noms différents pour chaque Daemon
    			Format inputTmp;
		        if(inputFormat == Format.Type.LINE) { // LINE
		        	inputTmp = new FormatLine(input.getFname() + "" + i);
				} else { // KV
		        	inputTmp = new FormatKV(input.getFname() + "" + i);
				}
		        Format interTmp = new FormatKV(inter.getFname() + "" + i);
		        nn.ajoutFragmentMachine(demonsToMachines.get(((DaemonImpl)d).getName()), inter.getFname(), interTmp.getFname());
				// on appelle le map sur le démon
				MapRunner mapRunner = new MapRunner(d, mr, inputTmp, interTmp, cb);
				mapRunner.start();
			}
	    	System.out.println("OK\n");
	
	    	
			// Puis on attends que tous les démons aient finis leur travail
	    	System.out.println("Attente de la confirmation des Daemons ...");
			try {
				int retour = cb.waitFinishedMap(numberOfMaps);
				if (retour == 0) {
					mapsfinis = false;
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}while (!mapsfinis);
    	System.out.println("OK\n");

    	
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

    public void initMachinesDaemons(){
		try {
			nn = ((NameNode) Naming.lookup("/localhost:1090/" + " NameNode" ));/* On considère que le nameNode est sur le même ordi que le job*/
			machines = nn.getMachines();
			for (Machine m : machines) {
				demonsToMachines.put(m.getNomDaemon(), m);
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	} 
}
