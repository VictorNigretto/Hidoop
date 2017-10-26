package ordo;

import formats.Format;
import map.MapReduce;

public class Job implements JobInterface {
	
	/*****************************************
	Attributs
	*****************************************/
	
	private static int numberOfReduces;
	private static int numberOfMaps;
	private static Format.Type inputFormat;
	private static Format.Type outputFormat;
	private static String inputFName;
	private static String outputFName;
	private static SortComparator sortComparator;
	
	/*****************************************
	Start Job (méthode principale)
	*****************************************/
	
    public void startJob (MapReduce mr) {
    	
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
    	
    }
    
    public Format.Type getOutputFormat(){
    	
    }
    
    public String getInputFname(){
    	
    }
    
    public String getOutputFname(){
    	
    }
    
    public SortComparator getSortComparator(){
    	
    }
    
}
