package ordo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

public interface RMInterface  extends Remote {
    public void DemonFonctionne(String nomDemon) throws RemoteException;

    public void supprimeDemon(String nomDemon)throws RemoteException;

    public Map<String, Boolean> getDemonsFonctionnent() throws RemoteException;

    public Collection<String> getDemons () throws RemoteException;
    }
