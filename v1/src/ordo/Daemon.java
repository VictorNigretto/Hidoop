package ordo;

import java.rmi.Remote;
import java.rmi.RemoteException;

import hdfs.Machine;
import map.Mapper;
import formats.Format;

// Le programme qu'on fait tourner sur les dataNode
// Il a pour but d'exécuter les maps qu'on lui envoie
public interface Daemon extends Remote {

	public Machine getMachine()throws RemoteException;
	// Lancer une map
	// On fournit le mapper, le reader, le writer, et le callback
	// Le callback sert à dire au job qu'on a terminé de faire le map
	public void runMap (Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException;
}
