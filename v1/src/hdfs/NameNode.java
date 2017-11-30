package hdfs;

import java.util.ArrayList;
import java.util.List;

public interface NameNode {

	/*Coucou :) on a ajouté des méthodes dont on aurait besoin dans le NameNode dans cette interface, vous pouvez bien sur les changer ;) */
	/* Coucou à vous ! :D ok super on s'en occupe =) */
	/* Si ça vous dérange pas, on va un petit peu modifier la spec ^^ */
		
	/** Renvoie la liste des fragments associés à un fichier sur HDFS
	 * La collection est vide si le fichier n'est pas présent.
	 * Les fragments sont numérotés ainsi : nomFichier + i où i est le numéro du fragment
	 */
	public List<String> getFragments(String nomFichier);
	
	/** Renvoie la machine la plus âpte à être utilié pour accèder à ce fragment.
	 * La collection replicationUtilisees sert si jamais la machine ne fonctionne pas :
	 * On peut rapeller cette méthode en mettant les machines ne fonctionnant pas dans replicationsUtilisees :
	 * ça nous assure que la prochaine machine n'appartiendra pas à cette liste.
	 * 
	 * Si il ne reste plus de machines utilisables, on renvoie une collection vide.
	 */
	public String getMachineFragment(String nomFragment, List<String> replicationsUtilisees);
}





