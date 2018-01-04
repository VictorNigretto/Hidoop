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
        /* On ajoute le RessourceManager à l'annuaire */
        String[] cmdRm = {"setUp.txt"};
        RessourceManager resMan = null;
        try {
            resMan = RessourceManager.lancerRM(cmdRm);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        /* On lance hdfs ( ce qui lance aussi les daemons*/
        LanceurHDFS.main(args);

       /* On lancer le RM */
        try {

            RessourceManager.main(resMan);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        /* On demande à l'utilisateur s'il veut utiliser hdfs ou hidoop */
        TerminalHDFS.main(args);
    }



}
