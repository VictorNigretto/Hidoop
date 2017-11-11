package ordo;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Annuaire {

    public static void main(String[] args) {
        System.out.println("Lancement de l'annuaire ...");
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("OK");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
