package hdfs;

import java.util.ArrayList;
import java.util.List;

/* Classe permettant de gérer une machine distante */
public class Fichier {
	
	/*****************************************
	ATTRIBUTS
	*****************************************/

	private String nom;
	private int nbFragments;
	
	/*****************************************
	CONSTRUCTEUR
	*****************************************/

	public Fichier(String nom) {
		this.nom = nom;
		nbFragments = -1;
	}
	
	/*****************************************
	METHODES
	*****************************************/
	
	public String getFragment(int num) {
		return nom + "" + num;
	}

	public List<String> getFragments() {
		List<String> l = new ArrayList<>();
		
		for(int i = 0; i < nbFragments; i++) {
			l.add(this.getFragment(i));
		}
		
		return l;
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
	public int getNbFragments() {
		return nbFragments;
	}
	public void setNbFragments(int nbFragments) {
		this.nbFragments = nbFragments;
	}
	
}
