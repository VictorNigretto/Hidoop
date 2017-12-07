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
	private String nomDaemon;
	private int port;
	private boolean alive;
	private List<String> fragments;
	
	/*****************************************
	CONSTRUCTEUR
	*****************************************/

	public Machine(String nom, int port, String nomDeamon) {
		this.nom = nom;
		this.port = port;
		this.nomDaemon = nomDeamon;
		this.alive = true;
		this.fragments = new ArrayList<>();		
	}

	/*****************************************
	METHODES
	*****************************************/
	
	public boolean containsFragment(String nomFragment) {
		for(String f : fragments) {
			// Si on possède un fragment du même nom ...
			if(f.equals(nomFragment)) {
				return true;
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
	public String getNomDaemon() {
		return nomDaemon;
	}
	public void setNomDaemon(String nomDaemon) {
		this.nomDaemon = nomDaemon;
	}

}
