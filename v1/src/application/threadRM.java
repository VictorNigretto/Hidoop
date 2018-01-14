package application;
import ordo.RessourceManager;


public class threadRM extends Thread{
    private  String[] commande;

    public threadRM(String[] cmdRm) {
        super();
        this.commande = cmdRm;
    }
    public void  run() {
    	RessourceManager.main(commande);
    }
}
