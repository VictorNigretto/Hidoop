package formats;


import java.io.*;
import java.util.ArrayList;

import util.Message;
import formats.Format.OpenMode;
import formats.Format.Type;

public class FormatLine implements Format {
	
// Essayer de factoriser en classe générique classes envoi et reception

	private File fileRead;
	private FileInputStream fis;
	private ObjectInputStream ois;
	private File fileWrite;
	private FileOutputStream fos;
	private ObjectOutputStream oos;
	
	private boolean OpenR = false;
	private boolean OpenW = false;
	
	private ArrayList<String> lines;
	private ArrayList<KV> KVs;
	
	// soit Message m mais attention, ou un par type ???

	private int port;
	private long index = 1;
	private String fname;	// nom du fichier

	public FormatLine(String fname, int port) {
		// Mettre le port en  parametre
		this.fname = fname;
		this.port = port;
	}
	
	public void open(OpenMode mode)  {
		try {
			//pas d 'ouverture de descripteur en lecture, envoyer copie fichier ?
			// Ouvrir pour chaques ecriture/lecture, ou une seule fois?
			// Creation fichier resultat dans Format ou serveur si ouverture a chaque fois, creer linesdans read?
		String filePath;
		Message m = new Message();
		m.openClient(port);
		if (mode == OpenMode.R) {
			// Récupèrer contenu fichier et le découper en lignes
			m.send(Commande.CMD_OPEN_R);
			m.send(fname);		//précisez le fichier dont on veut obtenir le path
			//  récupérer PATH du fichier dans le server,ou daemon et serveur au meme endroit?		
			filePath = (String) m.receive();
			fileRead = new File(filePath);
			fis = new FileInputStream(fileRead);
			ois = new ObjectInputStream(fis);
			Type fmt = (Type) ois.readObject();
			lines = (ArrayList<String>)ois.readObject();

			OpenR = true;

			}
		if (mode == OpenMode.W) {
			// Créer le fichier résultat dans format ou serveur?
			m.send(Commande.CMD_OPEN_W);
			m.send(fname);
			filePath = (String) m.receive();
			fileWrite = new File(filePath);
			fos = new FileOutputStream(fileWrite,true);
			oos = new ObjectOutputStream(fos);
			OpenW = true;

			KVs = new ArrayList<KV>();
			oos.writeObject(Type.KV);

			OpenR = true;
		}
		m.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		//mCMD.send(Commande.CMD_CLOSE,port);
		// fermer sock normalement je pense ou descripteurs
		try {
			if (OpenR) {
				ois.close();
				fis.close();
			}
			if (OpenW) {
				oos.writeObject(KVs);
				oos.close();
				fos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public KV read() {
		// Créer KV index + ligne à index
		KV kv = new KV();

		kv =new KV(Integer.toString((int) index), lines.get((int)index - 1));
		index++;

		return kv;
	}

	@Override
	public void write(KV record) {
		KVs.add(record);

	}

	@Override
	public long getIndex() {
		return this.index;

	}

	@Override
	public String getFname() {
		return this.fname;

	}

	@Override
	public void setFname(String fname) {
		this.fname = fname;
	}
	
}
