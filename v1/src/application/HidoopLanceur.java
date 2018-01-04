package application;

import ordo.RessourceManager;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HidoopLanceur {
    /**
     * Cette classe permet de lancer Hidoop
     */

    public static void main (String[] args) {
        /* On lance hdfs ( ce qui lance aussi les daemons*/
        LanceurHDFS.main(args);

        /* On ajoute le RessourceManager à l'annuaire  et on le lance*/
        try {
            RessourceManager.main(args);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        /* On demande à l'utilisateur s'il veut utiliser hdfs ou hidoop */
        TerminalHDFS.main(args);
    }



}
