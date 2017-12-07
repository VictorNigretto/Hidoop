package ordo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;

public class CallBackImpl extends UnicastRemoteObject implements  CallBack {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6711165414602374343L;
	static Semaphore nbMapsFinished;
    static Thread waiting = new Thread(new Attendre(),"1") ;
	static Thread sleeping = new Thread(new Dormir(),"0");

    public CallBackImpl() throws RemoteException {
        super();
        nbMapsFinished = new Semaphore(0);
    }

    /*public void run() {
    	Runnable dormir = new Runnable() {
    		public void run() {
    			
    		}
    	}
    }*/
    @Override
    // Permet à un démons de confier qu'il a bien terminé son traitement de map
    public void confirmFinishedMap() throws InterruptedException, RemoteException {
        nbMapsFinished.release();
    }

    @Override
    public void waitFinishedMap(int nb) throws InterruptedException, RemoteException {
    	
    
        for(int i = 0; i < nb; i++) {
			waiting.start();
			sleeping.start();

			System.out.println((i+1) + " maps se sont finis.");
		}
		
    }


}
