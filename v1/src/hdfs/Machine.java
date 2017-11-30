package hdfs;

import java.util.ArrayList;
import java.util.List;

/* Classe permettant de gérer une machine distante */
public class Machine {
	
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
	
	/* Renvoie vrai si cette machine contient ce fragment */
	public boolean containsFragment(String nomFragment) {
		for(String s : fragments) {
			if(s.equals(nomFragment)) {
				return true;
			}
		}
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
