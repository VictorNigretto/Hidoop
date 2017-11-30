package hdfs;

import java.util.ArrayList;

public interface NameNode {
	
	/*Coucou :) on a ajouté des méthodes dont on aurait besoin dans le NameNode dans cette interface, vous pouvez bien sur les changer ;) */
	
	/** Methode qui permet de supprimer un data node (dans le cas ou il ne dit pas s'il est vivant **/
	public void OublierDataNode(String nomMachine);
	
	/** Methode qui permet d'ajouter un nouveau DataNode **/
	public void AjouterDataNode(String nomMachine);
	
	/** renvoie la liste des machines qui permettent de travailler sur un fichier de nom "nomFichier" **/
	public ArrayList<String> getMachines(String nomFichier);
	
	/** renvoie un nouveau nom de machine correspondant à une machine qui marche et qui a le fragment "nomFragment" **/
	public String changeMachine(String nomMachine, String nomFragment);	
}
