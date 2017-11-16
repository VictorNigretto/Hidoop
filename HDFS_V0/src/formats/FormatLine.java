package formats;


import java.io.*;
import java.net.InetAddress;
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
	
	private String filePath;
	
	public ArrayList<Object> lines;
	private ArrayList<Object> KVs;


	private int port;

	// soit Message m mais attention, ou un par type ???

	private long index = 1;
	private String fname;	// nom du fichier

	public FormatLine(String fname) {
		// Mettre le port en  parametre
		this.fname = fname;

	}
	
	public void open(formats.Format.OpenMode mode)  {
		try {
			//pas d 'ouverture de descripteur en lecture, envoyer copie fichier ?
			// Ouvrir pour chaques ecriture/lecture, ou une seule fois?
			// Creation fichier resultat dans Format ou serveur si ouverture a chaque fois, creer linesdans read?
		String filePath;
		Message m = new Message();
		m.openClient(port);
		if (mode == OpenMode.R) {
			// Récupèrer contenu fichier et le découper en lignes
			//  récupérer PATH du fichier dans le server,ou daemon et serveur au meme endroit?



			System.out.println("affichage du fname : "+fname);
			String nomMachine = InetAddress.getLocalHost().getHostName();

			fileRead = new File(fname);
			fileRead.setReadable(true);

			fis = new FileInputStream(fileRead);
			ois = new ObjectInputStream(fis);
			System.out.println("bonjour");
			lines = (ArrayList<Object>)ois.readObject();
			OpenR = true;

			}
		if (mode == OpenMode.W) {
			// Créer le fichier résultat dans format ou serveur?
			fileWrite = new File(fname);

			fos = new FileOutputStream(fileWrite,true);
			oos = new ObjectOutputStream(fos);
			OpenW = true;

			KVs = new ArrayList<Object>();
			KVs.add(Type.KV);

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
		KV kv = null;
		if (index < (lines.size())) {
			kv = new KV(( Integer.toString((int) index)), (String) lines.get((int) index));
		}
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
