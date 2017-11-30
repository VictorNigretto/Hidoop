package hdfs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NameNodeImpl implements NameNode {
	
	/*****************************************
	ATTRIBUTS
	*****************************************/
	
	private int facteurDeReplication;
	private List<String> nomMachines;
	private Map<String, Integer> portsMachines;
	
	private List<String> fichiers;
	private Map<String, List<String>> fragmentsParFichier;
	
	private Map<String, List<String>> fragmentParMachines;
	
	/*****************************************
	CONSTRUCTEUR
	*****************************************/
	
	/*****************************************
	MAIN
	*****************************************/
	
	/*****************************************
	METHODES
	*****************************************/
	
	/*****************************************
	GETS && SETS
	*****************************************/
}
