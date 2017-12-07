package ordo;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

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
	
	public Daemon getDeamon() {
		return deamon;
	}

	public void setDeamon(Daemon deamon) {
		this.deamon = deamon;
	}

	public Mapper getM() {
		return m;
	}

	public void setM(Mapper m) {
		this.m = m;
	}

	public Format getReader() {
		return reader;
	}

	public void setReader(Format reader) {
		this.reader = reader;
	}

	public Format getWriter() {
		return writer;
	}

	public void setWriter(Format writer) {
		this.writer = writer;
	}

	public CallBack getCb() {
		return cb;
	}

	public void setCb(CallBack cb) {
		this.cb = cb;
	}

	public void run() {
		
		try {
			this.deamon.runMap(this.m, this.reader, this.writer, this.cb);
			
		} catch (RemoteException e) {
			try {
				/*NameNode nn = (NameNode) Naming.lookup("/localhost:1090/" + " NameNode" );
				List<Machine> machines = nn.getMachines();		
				int i = 0;
				while (machines.get(i).getNomDaemon().equals(((DaemonImpl)deamon).getName()) && i<machines.size() ) {
					i++;
				}	
				if (!listeMachinesPanne.contains(machines.get(i))) {
					listeMachinesPanne.add(machines.get(i));
				}
				Machine machine = nn.getMachineFragment(reader.getFname(), listeMachinesPanne);	*/
				/*this.deamon = new DaemonImpl(machine.getNomDaemon());
				
				this.deamon.runMap(this.m, this.reader, this.writer, this.cb);
				*/
				
				// On demande au NameNode une autre machine pour savoir qui est le nouveau démon
				NameNode nn = (NameNode) Naming.lookup("/localhost:1199/" +"NameNode");
				listeMachinesPanne.add(((DaemonImpl) deamon).getMachine());
				Machine machine = nn.getMachineFragment(reader.getFname(), listeMachinesPanne);
				String newNomDaemon = machine.getNomDaemon();
				
				//On se connecte à ce nouveau démon
				this.deamon = (Daemon) Naming.lookup("/" + machine.getNom()+ "/" + newNomDaemon);
				
				// On recommence ce qu'on faisait avant
				this.start();
			} catch (MalformedURLException | RemoteException | NotBoundException e1) {
				e1.printStackTrace();
			} 
		}
		
	}
}
