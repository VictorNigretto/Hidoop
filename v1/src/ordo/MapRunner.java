package ordo;

import java.rmi.RemoteException;

import formats.Format;
import hdfs.Machine;
import map.Mapper;

public class MapRunner extends Thread {
	
	Daemon deamon; //deamon sur lequel on va lancer le runMap
	Mapper m; //map à lancer
	Format reader, writer; //les formats de lecture et d'écriture
	CallBack cb;
	
	public MapRunner(Daemon deamon, Mapper m, Format reader, Format writer, CallBack cb){
		this.deamon = deamon;
		this.m = m;
		this.reader = reader;
		this.writer = writer;
		this.cb = cb;
	}
	
	public void run() {
		try {
			this.deamon.runMap(this.m, this.reader, this.writer, this.cb);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
}
