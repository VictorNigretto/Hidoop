package ordo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;

public class CallBackImpl extends UnicastRemoteObject implements CallBack {

    private static final long serialVersionUID = 1L;

    private Semaphore nbMapsFinished;

    public CallBackImpl() throws RemoteException {
        super();
        nbMapsFinished = new Semaphore(0);
    }

    @Override
    // Permet à un démons de confier qu'il a bien terminé son traitement de map
    public void confirmFinishedMap() throws InterruptedException {
        nbMapsFinished.release();
    }

    @Override
    public void waitFinishedMap(int nb) throws InterruptedException {
        for(int i = 0; i < nb; i++) {
			try {
				nbMapsFinished.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
}
