package application;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/* Classe pour run l'annuaire */
public class RunAnnuaire {

    public static void main(String[] args) {
        // Lancer l'annuaire
        System.out.println("Lancement de l'annuaire ...");
        try {
            Registry registry = LocateRegistry.createRegistry(1199);
            System.out.println("OK");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        while(true);
    }

}
