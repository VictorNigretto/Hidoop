package ordo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import hdfs.Machine;
import hdfs.NameNode;

public interface RMInterface  extends Remote {
    public void DemonFonctionne(String nomDemon) throws RemoteException;

    public void supprimeDemon(String nomDemon)throws RemoteException;

    public Map<String, Boolean> getDemonsFonctionnent() throws RemoteException;

    public Collection<Machine> getMachines () throws RemoteException;

    public ArrayList<String> RecupererNomDemons(String Fname) throws RemoteException;

    public String RecupererDemonFragment(String Fname) throws RemoteException;

    public void ajouterFichier(String Fname) throws RemoteException;

    public void enleverFichier(String Fname) throws RemoteException;
    
    public void setNotreNameNode(NameNode nn) throws RemoteException;
    
    public NameNode getNotreNameNode() throws RemoteException;

    }
