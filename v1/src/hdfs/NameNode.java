package hdfs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ordo.Daemon;

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
	 * La collection machineInutilisables sert si jamais la machine ne fonctionne pas :
	 * On peut rapeller cette méthode en mettant les machines ne fonctionnant pas dans machineInutilisables :
	 * ça nous assure que la prochaine machine n'appartiendra pas à cette liste.
	 * 
	 * Si il ne reste plus de machines utilisables, on renvoie une collection vide.
	 */
	public Machine getMachineFragment(String nomFragment, List<Machine> machineInutilisables);
	
	/** Renvoie la liste des machines pour accèder à ce fragment. */
	public List<Machine> getAllMachinesFragment(String nomFragment);
	
	/** Renvoie la liste des fragments d'un fichier contenu dans une machine */
	public List<String> getAllFragmentFichierMachine(Machine m, String nomFichier);
	
	/** Renvoie la liste des machines qui contiennent au moins un fragment du fichier donné en paramètre*/
	public List<Machine> getMachinesFichier(String nomFichier);

	/** Renvoie la liste des toutes les machines*/
	public List<Machine> getMachines();
	/** Renvoie la liste des démons (dans le même ordre que les machines */
	public List<String> getDaemons();
}





