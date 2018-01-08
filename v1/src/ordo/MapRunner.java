package ordo;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import formats.Format;
import hdfs.Machine;
import map.Mapper;

public class MapRunner extends Thread {

	/*********************************
	 * ATTRIBUTS & CONSTRUCTEUR
	 *********************************/

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

	/*********************************
	 * METHODES
	 *********************************/
	public void run() {
		try {
			this.deamon.runMap(this.m, this.reader, this.writer, this.cb);
		} catch (RemoteException e) {
			// Dans ce cas on essaye de changer de Daemon
			System.out.println("Veuillez patienter un moment, nous essayons un autre Daemon");
			try {
				RMInterface ResMan = (RMInterface) Naming.lookup("//localhost:1199/RessourceManager");
				String nomDemon = ResMan.RecupererDemonFragment(this.reader.getFname());
				if (nomDemon == null) {
					System.out.println("Il n'y a plus de démons fonctionnels pouvant effectuer le map sur ce fragment de fichier");
				}else {
					this.setDeamon((Daemon) Naming.lookup(("//localhost:1199/" + nomDemon)));
					this.start();
				}

			} catch (NotBoundException e1) {
				e.printStackTrace();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (RemoteException e1) {
				System.out.println("Meme le RessourceManager est mort, nous ne pouvons plus rien faire, veuillez nous excuser");
			}

		}
		
	}

	/*********************************
	 * GETTERS ET SETTERS
	 *********************************/


	public Daemon getDeamon() {
		return deamon;
	}

	public void setDeamon(Daemon deamon) {
		this.deamon = deamon;
	}

	public void setM(Mapper m) {
		this.m = m;
	}

	public void setReader(Format reader) {
		this.reader = reader;
	}

	public void setWriter(Format writer) {
		this.writer = writer;
	}

	public void setCb(CallBack cb) {
		this.cb = cb;
	}

	public Mapper getM() {
		return m;

	}

	public Format getReader() {
		return reader;
	}

	public Format getWriter() {
		return writer;
	}

	public CallBack getCb() {
		return cb;
	}
}
