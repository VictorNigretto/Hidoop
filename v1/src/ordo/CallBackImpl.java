package ordo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;
import java.util.concurrent.CountDownLatch;
public class CallBackImpl extends UnicastRemoteObject implements  CallBack {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6711165414602374343L;
	static Semaphore nbMapsFinished;
    private Thread waiting; 
	private Thread sleeping; 
    private int nbDaemons;
    
    public CallBackImpl() throws RemoteException {
        super();
        nbMapsFinished = new Semaphore(0);
    }
    

	Runnable dormir = new Runnable() {
		public void run() {
			try{
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	
	Runnable attendre = new Runnable() {	
		public void run() {
			int nbMapsFin = 0;
			try {		
				for(int i = 0; i < nbDaemons; i++) {
					nbMapsFinished.acquire();
					nbMapsFin = i;
				}
				sleeping.interrupt();

			}catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
				System.out.println((nbMapsFin+1) + " maps se sont finis.");
			}
		}	
	};
   
	
	
    @Override
    // Permet à un démons de confier qu'il a bien terminé son traitement de map
    public void confirmFinishedMap() throws InterruptedException, RemoteException {
        nbMapsFinished.release();
    }

    @Override
    public int waitFinishedMap(int nb) throws InterruptedException, RemoteException {
    	waiting = new Thread(attendre,"1") ;
    	sleeping = new Thread(dormir,"0");
        nbDaemons = nb;
		waiting.start();
		sleeping.start();
		nbDaemons = nbMapsFinished.availablePermits();
		System.out.println("Il y a "+ nbDaemons + " Démons qui ont plantés");
		return nbDaemons;
    }
	


}



