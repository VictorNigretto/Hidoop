package hdfs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AbstractDocument.BranchElement;

public class NameNodeImpl implements NameNode {
	
	/*****************************************
	ATTRIBUTS
	*****************************************/
	private static List<String> servers;
	private static List<Integer> ports;

	@Override
	public void OublierDataNode(String nomMachine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void AjouterDataNode(String nomMachine) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<String> getMachines(String nomFichier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String changeMachine(String nomMachine, String nomFragment) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Initialisation de la liste des serveurs
	public static void load(String fichierSetup){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fichierSetup));
		} catch (FileNotFoundException e) {
			System.out.println("Fichier " + fichierSetup + " introuvable");
		}
		String ligne;
		servers = new ArrayList<>();
		ports = new ArrayList<>();
		try {
			while ((ligne = br.readLine()) != null){
				String[] machine = ligne.split(" ");
				ports.add(Integer.parseInt(machine[0]));
				servers.add(machine[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	System.out.println("Liste des machines et des ports initialisées");
	}

	public static void main(String[] args) {
		
		//Récupérer les serveurs et les numéros de port depuis le fichier spécifié
		if(args.length != 1){
			System.out.println("Usage : java NameNodeImple <file>");
		} else {
			load(args[0]);
			
		}
	}

	@Override
	public Collection<String> getFragments(String nomFichier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMachineFragment(String nomFragment, Collection<String> replicationsUilisees) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getFragments(String nomFichier) {
		// TODO Auto-generated method stub
		return null;
	}
}
