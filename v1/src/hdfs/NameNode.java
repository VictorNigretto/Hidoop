package hdfs;

import java.util.ArrayList;

public interface NameNode {

	/*Coucou :) on a ajouté des méthodes dont on aurait besoin dans le NameNode dans cette interface, vous pouvez bien sur les changer ;) */
	/* Coucou à vous ! :D ok super on s'en occupe =) */
	/* Si ça vous dérange pas, on va un petit peu modifier la spec ^^ */
	
	// On peut pas vraiment supprimer un dataNode, ou alors le nom n'est pas exactement ce que vous vouliez dire
	/** Methode qui permet de supprimer un data node (dans le cas ou il ne dit pas s'il est vivant) **/
	public void OublierDataNode(String nomMachine);
	
	// idem
	/** Methode qui permet d'ajouter un nouveau DataNode **/
	public void AjouterDataNode(String nomMachine);
	
	// ok ça c'est cool
	/** renvoie la liste des machines qui permettent de travailler sur un fichier de nom "nomFichier" **/
	public ArrayList<String> getMachines(String nomFichier);
	
	// idem
	/** renvoie un nouveau nom de machine correspondant à une machine qui marche et qui a le fragment "nomFragment" **/
	public String changeMachine(String nomMachine, String nomFragment);
	
	/* Je vous propose la spécification suivante, si vous le voulez bien =) */
	
	/** Renvoie la liste des fragments associés à un fichier sur HDFS
	 * La collection est vide si le fichier n'est pas présent.
	 * Les fragments sont numérotés ainsi : nomFichier + i où i est le numéro du fragment
	 */
	public Collection<String> getFragments(String nomFichier);
	
	/** Renvoie la machine la plus âpte à être utilié pour accèder à ce fragment.
	 * La collection replicationUtilisees sert si jamais la machine ne fonctionne pas :
	 * On peut rapeller cette méthode en mettant les machines ne fonctionnant pas dans replicationsUtilisees :
	 * ça nous assure que la prochaine machine n'appartiendra pas à cette liste.
	 * 
	 * Si il ne reste plus de machines utilisables, on renvoie une collection vide.
	 */
	public String getMachineFragment(String nomFragment, Collection<String> replicationsUilisees);
}
