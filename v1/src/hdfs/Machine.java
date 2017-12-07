package hdfs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/* Classe permettant de gérer une machine distante */
public class Machine implements Serializable{
	
	/*****************************************
	ATTRIBUTS
	*****************************************/

	private String nom;
	private int port;
	private boolean alive;
	private List<String> fragments;
	
	/*****************************************
	CONSTRUCTEUR
	*****************************************/

	public Machine(String nom, int port) {
		this.nom = nom;
		this.port = port;
		this.alive = true;
		this.fragments = new ArrayList<>();		
	}
	
	/*****************************************
	METHODES
	*****************************************/
	
	public boolean containsFragment(String nomFragment) {
		for(String f : fragments) {
			// Si un fragment commence par ce nom
			if(f.startsWith(nomFragment)) {
				// alors on vérifie que c'est bien un fragment, et pas un autre fichier
				String fin = f.replaceFirst(nomFragment, "");
				
				try {
					Integer.parseInt(fin);
					return true;
				} catch (NumberFormatException e) {
					// Si ça lève une exception, ce n'est pas un fragment de ce fichier
				}
			}
		}
		// Si on a parcouru tous les fichiers sans le trouver, alors c'est qu'il n'y en a pas !
		return false;
	}
	
	/*****************************************
	GETS && SETS
	*****************************************/

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public List<String> getFragments() {
		return fragments;
	}
	public void setFragments(List<String> fragments) {
		this.fragments = fragments;
	}
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}
