package application;
import ordo.RMInterface;
import ordo.RessourceManager;

import java.rmi.RemoteException;

public class threadRM extends Thread{
    private RMInterface monRM;

    public threadRM(RMInterface resMan) {
        super();
        this.monRM = resMan;
    }
    static public void  run (RMInterface resMan) {
        try {
            RessourceManager.main(resMan);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
