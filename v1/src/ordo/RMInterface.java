package ordo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public interface RMInterface  extends Remote {
    public void DemonFonctionne(String nomDemon) throws RemoteException;

    public void supprimeDemon(String nomDemon)throws RemoteException;

    public Map<String, Boolean> getDemonsFonctionnent() throws RemoteException;

    public Collection<String> getDemons () throws RemoteException;

    public ArrayList<String> RecupererNomDemons(String Fname) throws RemoteException;

    public String RecupererDemonFragment(String Fname) throws RemoteException;

    public void ajouterFichier(String Fname) throws RemoteException;

    public void enleverFichier(String Fname) throws RemoteException;

    }
