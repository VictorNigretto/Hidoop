package hdfs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.text.AbstractDocument.BranchElement;

public class NameNodeImpl implements NameNode {
	
	/*****************************************
	ATTRIBUTS
	*****************************************/	
	
	private final static int facteurDeReplication = 3;
	
	private List<Machine> machines;
	private Map<String, Fichier> fichiers; // code un fichier, la clé est son nom, la valeur est un objet Fichier
	
	/*****************************************
	CONSTRUCTEUR
	*****************************************/
	
	//Initialisation de la liste des serveurs
	public NameNodeImpl(String fichierSetup){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fichierSetup));
		} catch (FileNotFoundException e) {
			System.out.println("Fichier " + fichierSetup + " introuvable");
		}
		String ligne;
		machines = new ArrayList<>();
		try {
			while ((ligne = br.readLine()) != null){
				String[] machine = ligne.split(" ");
				machines.add(new Machine(machine[1], Integer.parseInt(machine[0])));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	System.out.println("Liste des machines et des ports initialisées");
	}
	
	/*****************************************
	MAIN
	*****************************************/
	
	public static void main(String[] args) {
		//Récupérer les serveurs et les numéros de port depuis le fichier spécifié
		if(args.length != 1){
			System.out.println("Usage : java NameNodeImple <file>");
		} else {
			NameNode monNameNode = new NameNodeImpl(args[0]);
		}
	}

	/*****************************************
	METHODES
	*****************************************/
	
	public List<String> getFragments(String nomFichier) {
		return fichiers.get(nomFichier).getFragments();
	}

	public String getMachineFragment(String nomFragment, List<String> replicationsUtilisees) {
		// Récupérer la liste des machines contenant ce fragment
		
		// Faire la différence ensembliste avec les replicationsUtilisees
		
		// Trouver la machine la moins pleine
	}

	/*****************************************
	GETS && SETS
	*****************************************/
}
