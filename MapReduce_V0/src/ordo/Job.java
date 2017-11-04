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


public class Job extends UnicastRemoteObject implements JobInterface, CallBack  {
	
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

	private Semaphore nbMapsFinished;

	/*****************************************
	Constructeurs
	*****************************************/

	// Constructeur vide avec les données minimums
	// Le reste à étant à remplir par l'utilisateur
	public Job() throws RemoteException {
		super();
		this.numberOfMaps = 10; //TODO
		this.numberOfReduces = 1; //Pour la V0 uniquement
		this.sortComparator = new SortComparatorLexico(); //TODO

        this.nbMapsFinished = new Semaphore(0);
	}

	// On peut aussi ajouter directement l'input
	// L'output étant à remplir par l'utilisateur
	public Job(Format.Type inputFormat, String inputFName) throws RemoteException{
		this();
		this.inputFormat = inputFormat;
		this.inputFName = inputFName;
	}

	// Et on peut aussi tout spécifier
	public Job(Format.Type inputFormat, Format.Type outputFormat, String inputFName, String outputFName) throws RemoteException{
		this(inputFormat, inputFName);
		this.outputFormat = outputFormat;
		this.outputFName = outputFName;
	}
	
	/*****************************************
	Start Job (méthode principale)
	*****************************************/
	
    public void startJob (MapReduce mr) {
    	// 0) déterminer où lancer les maps
        // 1) lancer les maps sur tous les chunks du fichier
        // 2) les récupérer quand ils ont finis
        // 3) les concatener dans le fichier résultat avec le reduce qui s'exécutera sur tous les résultats des maps

		// Créons le format d'input
		Format input;
        if(inputFormat == Format.Type.LINE) { // LINE
			input = new FormatLineLocal(inputFName);
		} else { // KV
			input = new FormatKVLocal(inputFName);
		}
		// Et celui d'output
		Format output = new FormatLineLocal(outputFName);

    	// récupérer les chunks du fichier x)
    	// Ils se trouvent sur les Daemons ! Comment-est-ce que j'y ai accès ?
    	List<Daemon> demons = new ArrayList<Daemon>();
        List<Format> formatMapResultats = new ArrayList<>(); // là où l'on va écrire les résultats des maps
    	for(int i = 0; i < this.numberOfMaps; i++) {
    		try {
    		    // On va récupérer les Démons en RMI sur un annuaire
				demons.add((Daemon) Naming.lookup("//localhost/premierDaemon")); // TODO
				// initialiser les formats
				formatMapResultats.add(new FormatKVLocal());
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}

    	// Puis on va lancer les maps sur les différents démons
        for(int i = 0; i < this.numberOfMaps; i++) {
        	Daemon d = demons.get(i);
        	Format res = formatMapResultats.get(i);
			try {
				d.runMap(mr, input, res, this);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		// Puis on attends que tous les démons aient finis leur travail
        for(int i = 0; i < numberOfMaps; i++) {
			try {
				nbMapsFinished.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// On utilise HDFS

		// Puis on applique le reduce sur tous les résultats des maps
        for(Format res : formatMapResultats) {
    		mr.reduce(res, output);
		}

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

	// Permet à un démons de confier qu'il a bien terminé son traitement de map
	public void confirmFinishedMap() throws InterruptedException {
		nbMapsFinished.release();
	}

    public void setNumberOfReduces(int tasks){
    	this.numberOfReduces = tasks;
    }
    
    public void setNumberOfMaps(int tasks) {
    	this.numberOfMaps = tasks;
    }
    
    public void setInputFormat(Format.Type ft){
    	this.inputFormat = ft;
    }
    
    public void setOutputFormat(Format.Type ft){
    	this.outputFormat = ft;
    }
    
    public void setInputFname(String fname){
    	this.inputFName = fname;
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
