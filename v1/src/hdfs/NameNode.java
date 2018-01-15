package hdfs;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ordo.Daemon;

public interface NameNode extends Remote {
	
	/*
    public enum Commande { CMD_GETfragments,
    					   CMD_GETmachinesFragments,
    					   CMD_GETallMachinesFragments,
    					   CMD_GETallFragmentFichierMachine,
    					   CMD_GETMachinesFichier,
    					   CMD_GETmachines,
    					   CMD_GETdaemons};
    */


	/*Coucou :) on a ajouté des méthodes dont on aurait besoin dans le NameNode dans cette interface, vous pouvez bien sur les changer ;) */
	/* Coucou à vous ! :D ok super on s'en occupe =) */
	/* Si ça vous dérange pas, on va un petit peu modifier la spec ^^ */
		
	/** Renvoie la liste des fragments associés à un fichier sur HDFS
	 * La collection est vide si le fichier n'est pas présent.
	 * Les fragments sont numérotés ainsi : nomFichier + i où i est le numéro du fragment
	 */
	public List<String> getFragments(String nomFichier) throws RemoteException;
	
	/** Renvoie la machine la plus âpte à être utilié pour accèder à ce fragment.
	 * La collection machineInutilisables sert si jamais la machine ne fonctionne pas :
	 * On peut rapeller cette méthode en mettant les machines ne fonctionnant pas dans machineInutilisables :
	 * ça nous assure que la prochaine machine n'appartiendra pas à cette liste.
	 * 
	 * Si il ne reste plus de machines utilisables, on renvoie une collection vide.
	 */
	public Machine getMachineFragment(String nomFragment, List<Machine> machineInutilisables) throws RemoteException;
	
	/** Renvoie la machine qui contient le moins de fragments */
	public Machine getMachineMoinsPleine() throws RemoteException;

	
	/** Renvoie la liste des machines pour accèder à ce fragment. */
	public List<Machine> getAllMachinesFragment(String nomFragment) throws RemoteException;
	
	/** Renvoie la liste des fragments d'un fichier contenu dans une machine */
	public List<String> getAllFragmentFichierMachine(Machine m, String nomFichier) throws RemoteException;
	
	/** Renvoie la liste des machines qui contiennent au moins un fragment du fichier donné en paramètre*/
	public List<Machine> getMachinesFichier(String nomFichier) throws RemoteException;

	/** Renvoie la liste des toutes les machines*/

	public List<Machine> getMachines() throws RemoteException;
	
	/** Indique au NameNode que l'on rajoute un fichier à la base de données */
	public void ajoutFichierHdfs(String nomFichier) throws RemoteException;
	
	/** Indique au NameNode que machine possède fragment */
	public void ajoutFragmentMachine(Machine machine, String nomFichier, String nomFragment, int numeroFragment) throws RemoteException;
	
	/** Indique au NameNode que l'on supprime un fichier de la base de données */
	public void supprimeFichierHdfs(String nomFichier) throws RemoteException;

	/** Indique le facteur de replication des fragments */
	public int getFacteurdereplication() throws RemoteException;

	/** Indique si un fichier existe */
	public boolean fileExists(String fileName) throws RemoteException;
}





