package application;
import hdfs.NameNode;
import ordo.RMInterface;
import ordo.RessourceManager;

import java.rmi.RemoteException;

public class threadRM extends Thread{
    private  RMInterface monRM;

    public threadRM(RMInterface ResMan) {
        super();
        this.monRM = ResMan;
    }
    public void  run() {
        try {
            RessourceManager.main(monRM);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
