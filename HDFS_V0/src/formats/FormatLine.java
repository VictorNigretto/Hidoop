package formats;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import util.Message;
import formats.Format.OpenMode;

public class FormatLine implements Format {
	
// Essayer de factoriser en classe générique classes envoi et reception

	private File fileRead;
	private FileInputStream fr;
	private File fileWrite;
	private FileOutputStream fw;
	
	private boolean OpenR = false;
	private boolean OpenW = false;
	
	private String filePath;
	
	private String lines[];
	
	// soit Message m mais attention, ou un par type ???
	private Message<Commande> mCMD;
	private Message<String> mString;
	private Message<Type> mType;
	private Message<KV> mKV;

	private int port = 6666;
	private long index = 1;
	private String fname;	// nom du fichier
	private Type type;		// type du format
	
	public FormatLine(String fname, Type type) {
		// Mettre le port en  parametre
		this.fname = fname;
		this.type = type;
		this.mCMD = new Message<Commande>();
		this.mString = new Message<String>();
		this.mType = new Message<Type>();
		this.mKV = new Message<KV>();
	}
	
	public void open(OpenMode mode) {
		try {
			//pas d 'ouverture de descripteur en lecture, envoyer copie fichier ?
			// Ouvrir pour chaques ecriture/lecture, ou une seule fois?
			// Creation fichier resultat dans Format ou serveur si ouverture a chaque fois, creer linesdans read?
		if (mode == OpenMode.R) {
			// Récupèrer contenu fichier et le découper en lignes
			mCMD.send(Commande.CMD_OPEN_R,port);
			//mString.send(fname,port);		précisez le fichier dont on veut obtenir le path
			//  récupérer PATH du fichier dans le server,ou daemon et serveur au meme endroit?		
			filePath = mString.reception(port);
			fileRead = new File(filePath);
			fr = new FileInputStream(fileRead);
			OpenR = true;
			byte[] buf = new byte[(int) fileRead.length()];
			fr.read(buf);
			String fileContent = new String(buf);
   			lines = fileContent.split("\n");	
			}
		if (mode == OpenMode.W) {
			// Créer le fichier résultat dans format ou serveur?
			mCMD.send(Commande.CMD_OPEN_W,port);
			mString.send(fname,port);
			filePath = mString.reception(port);
			fileWrite = new File(filePath);
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
	public KV read() {
		// Créer KV index + ligne à index
		KV kv = new KV(String.valueOf(index),lines[(int)index-1]);
		index++;
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
