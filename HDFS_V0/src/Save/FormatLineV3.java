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
	private static FileInputStream fr;
	private File fileWrite;
	private static FileOutputStream fw;
	private static String lines[];
	
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
			// Creation fichier resultat dans Format ou serveur?
		if (mode == OpenMode.R) {
			// Récupèrer contenu fichier et le découper en lignes
			mCMD.send(Commande.CMD_OPEN_R,port);
			// envoie nom du fichier pour récupérer celui qui correspond
			String fileContent = mString.reception(port);
   			lines = fileContent.split("\n");	
			}
		if (mode == OpenMode.W) {
			// Créer le fichier résultat
			fileWrite = new File(fname + "-res");
			fw = new FileOutputStream(fileWrite,true);
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		//mCMD.send(Commande.CMD_CLOSE,port);
		// fermer sock normalement je pense ou descripteurs
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
