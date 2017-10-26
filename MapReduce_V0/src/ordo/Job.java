package ordo;

import java.rmi.RemoteException;

import formats.Format;
import map.MapReduce;
import java.rmi.server.UnicastRemoteObject;


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
	
	/*****************************************
	Constructeurs
	*****************************************/
	public Job() throws RemoteException {
		this.numberOfMaps = 10; //TODO
		this.numberOfReduces = 1; //Pour la V0 uniquement
		this.sortComparator = new SortComparatorImpl(); //TODO
	}
	
	public Job(Format.Type inputFormat, String inputFName) throws RemoteException{
		this();
		this.inputFormat = inputFormat;
		this.inputFName = inputFName;
	}
	
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
    	
    	// récupérer les chunks du fichier !
    	// Ils se trouvent sur les Daemons ! Comment-est-ce que j'y ai accès ?
    	for(int i = 0; i < this.numberOfMaps; i++) {
    		
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
