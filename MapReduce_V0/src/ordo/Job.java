package ordo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import formats.*;
import map.MapReduce;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;

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
		this.initMachines();
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

		this.outputFName = inputFName + "-res";
		this.interFName = inputFName + "-inter";
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

		// Créons le format d'input, intermédiaire et d'output pour le client et tous les démons
		Format input, inter, output;
        if(inputFormat == Format.Type.LINE) { // LINE
			input = new FormatLine(inputFName, Format.Type.LINE);
			inter = new FormatLine(interFName, Format.Type.LINE);
			output = new FormatLine(outputFName, Format.Type.LINE);
		} else { // KV
			input = new FormatKV(inputFName, Format.Type.KV);
			inter = new FormatKV(interFName, Format.Type.KV);
			output = new FormatKV(outputFName, Format.Type.KV);
		}

    	// récupérer la liste des démons sur l'annuaire
    	List<Daemon> demons = new ArrayList<>();
    	for(int i = 0; i < this.numberOfMaps; i++) {
    		try {
    		    // On va récupérer les Démons en RMI sur un annuaire
				// TODO => généraliser à plusieurs démons sur plusieurs machines
				demons.add((Daemon) Naming.lookup("//localhost/" + machines.get(i)));
				//demons.add((Daemon) Naming.lookup("//localhost/premierDaemon"));
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}

    	// On initialise le callback pour que les démons puissent renvoyer leurs résultats
		CallBack cb = null;
		try {
			cb = new CallBackImpl();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		// Puis on va lancer les maps sur les différents démons
		for(Daemon d : demons) {
			try {
				// on appelle le map sur le démon
				// on utilise le même format input et le même format output pour chacun
				// car par RMI on envoie des copies, et c'est lorsque les formats seront "open"
				// sur les différents démons, que s'effectuera le chargement des différents chunks
				d.runMap(mr, input, inter, cb);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		// Puis on attends que tous les démons aient finis leur travail
		try {
			cb.waitFinishedMap(numberOfMaps);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// On utilise HDFS pour récupérer le fichier résultat concaténé dans resReduce
		Format resReduce;
		if(inputFormat == Format.Type.LINE) {
			resReduce = new FormatLine("resReduceFormat", Format.Type.LINE);
		} else {
			resReduce = new FormatKV("resReduceFormat", Format.Type.KV);
		}
		HdfsRead(inter.getFname(), resReduce.getFname());

    	// On veut transformer ce fichier en un format local
        output.open(Format.OpenMode.R);

		// Puis on applique le reduce sur le résultat concaténé des maps
		// On stock le résultat dans l'output
		mr.reduce(resReduce, output);

		// On extrait une liste de notre format output pour pouvoir le trier
		List<KV> listeTriee = new ArrayList<>();
    	KV kv;
    	while((kv = output.read()) != null) {
    		listeTriee.add(kv);
		}
		listeTriee.sort((Comparator<? super KV>) sortComparator);

		// Puis on l'écrit dans le fichier de sortie
		File fOutput = new File(outputFName);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fOutput));
			for(KV ligne : listeTriee) {
				bw.write(ligne.k);
				bw.write(KV.SEPARATOR);
				bw.write(ligne.v);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
    	this.outputFName = fname + "-res";
		this.interFName = fname + "-inter";
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

    public void initMachines(){
    	this.machines = new ArrayList<String>();
    		machines.add("succube");
    		machines.add("lucifer");
    		machines.add("cthun");
	}
}
