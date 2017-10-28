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
		if (mode == OpenMode.R) {
			//Creer fichier
			fileRead = new File(fname);
			//Creer descripteur
			fr = new FileInputStream(fileRead);
			
			//ajout cond format line ou mauvaise endroit ; mettre dans FormatLine?
			// lecture texte en ligne
			/*int taille = (int) fileRead.length();
   			byte[] buf = new byte[taille];
   			fr.read(buf);
   			String text = new String(buf);
   			lines = text.split("\n");*/
			
			}
		if (mode == OpenMode.W) {
			fileWrite = new File(fname + "-res");
			fw = new FileOutputStream(fileWrite,true);
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
	}

	@Override
	public KV read() {
		KV kv = new KV();
		mCMD.send(Commande.CMD_READ,port);
		String fileContent = mString.reception(port);
		
		index++;
		return kv;
	}

	@Override
	public void write(KV record) {
		mCMD.send(Commande.CMD_WRITE,port);	
		mType.send(type,port);	
		mKV.send(record,port);
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
