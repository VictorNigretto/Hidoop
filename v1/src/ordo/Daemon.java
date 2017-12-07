package ordo;

import java.rmi.Remote;
import java.rmi.RemoteException;

import map.Mapper;
import formats.Format;
import hdfs.Machine;

// Le programme qu'on fait tourner sur les dataNode
// Il a pour but d'exécuter les maps qu'on lui envoie
public interface Daemon extends Remote {
	// Lancer une map
	// On fournit le mapper, le reader, le writer, et le callback
	// Le callback sert à dire au job qu'on a terminé de faire le map
	public void runMap (Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException;
	
	public Machine getMachine();
}
