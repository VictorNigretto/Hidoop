package formats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import util.Message;

import formats.Format.Commande;
import formats.Format.OpenMode;

public class FormatKV implements Format {

	private FileInputStream fr;
	private FileOutputStream fw;
	
	private boolean OpenR = false;
	private boolean OpenW = false;
	
	private Message<Commande> mCMD;
	private Message<String> mString;
	
	private String fname;
	private int port;
	private long index = 1;
	
	public FormatKV(String fname, int port) {
		this.fname = fname;
		this.port = port;
		this.mCMD = new Message<Commande>();
		this.mString = new Message<String>();
	}
	
	@Override
	public KV read() {
		KV kv = new KV();
		// TODO Auto-generated method stub
		try {
			ObjectInputStream ois = new ObjectInputStream (fr);
			kv = (KV) ois.readObject();
			ois.close();
			index++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return kv;
	}

	@Override
	public void write(KV record) {
		try {
			fw.write((record.k + KV.SEPARATOR + record.v + "\n").getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void open(OpenMode mode) {
		try {
			//pas d 'ouverture de descripteur en lecture, envoyer copie fichier ?
			// Ouvrir pour chaques ecriture/lecture, ou une seule fois?
			// Creation fichier resultat dans Format ou serveur si ouverture a chaque fois, creer linesdans read?
		String filePath;
		if (mode == OpenMode.R) {
			// Récupèrer contenu fichier et le découper en lignes
			mCMD.send(Commande.CMD_OPEN_R,port);
			//mString.send(fname,port);		précisez le fichier dont on veut obtenir le path
			//  récupérer PATH du fichier dans le server,ou daemon et serveur au meme endroit?		
			filePath = mString.reception(port);
			File fileRead = new File(filePath);
			fr = new FileInputStream(fileRead);
			OpenR = true;
			}
		if (mode == OpenMode.W) {
			// Créer le fichier résultat dans format ou serveur?
			mCMD.send(Commande.CMD_OPEN_W,port);
			mString.send(fname,port);
			filePath = mString.reception(port);
			File fileWrite = new File(filePath);
			fw = new FileOutputStream(fileWrite,true);
			OpenW = true;
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		//mCMD.send(Commande.CMD_CLOSE,port);
		// fermer sock normalement je pense ou descripteurs
		try {
			if (OpenR) {	
				fr.close();
			}
			if (OpenW) {	
				fw.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public long getIndex() {
		// TODO Auto-generated method stub
		return index;
	}

	@Override
	public String getFname() {
		// TODO Auto-generated method stub
		return fname;
	}

	@Override
	public void setFname(String fname) {
		// TODO Auto-generated method stub
		this.fname = fname;
	}

}
