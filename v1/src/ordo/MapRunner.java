package ordo;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import formats.Format;
import hdfs.Machine;
import hdfs.NameNode;
import map.Mapper;

public class MapRunner extends Thread {
	
	Daemon deamon; //deamon sur lequel on va lancer le runMap
	Mapper m; //map à lancer
	Format reader, writer; //les formats de lecture et d'écriture
	CallBack cb;
	ArrayList<Machine> listeMachinesPanne ;
	
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
			try {
				NameNode nn = (NameNode) Naming.lookup("/localhost:1090/" + " NameNode" );
				nn.getMachineFragment(reader.getFname(), deamon.getMachine());
			} catch (MalformedURLException | RemoteException | NotBoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
		
	}
}
