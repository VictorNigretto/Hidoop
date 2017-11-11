package ordo;
import java.io.Serializable;
import java.rmi.Remote;

public interface CallBack extends Remote, Serializable {
    // Permet à un démons de confier qu'il a bien terminé son traitement de map
	public void confirmFinishedMap() throws InterruptedException;

	// Permet de savoir si nb maps sont terminés
    public void waitFinishedMap(int nb) throws InterruptedException;
}
