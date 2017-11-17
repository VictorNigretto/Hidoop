package formats;


import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;

import util.Message;
import formats.Format.OpenMode;
import formats.Format.Type;

public class FormatLine implements Format {


	private FileReader fr;
	private BufferedReader br;
	private FileWriter fw;
	private ObjectOutputStream oos;
	
	private boolean OpenR = false;
	private boolean OpenW = false;

	public String[] lines;
	private ArrayList<Object> KVs;


	private int port;

	private long index = 1; // index de lecture
	private String fname;	// nom du fichier

	public FormatLine(String fname) {
		this.fname = fname;
	}
	
	public void open(formats.Format.OpenMode mode)  {
		try {
			// Récupérer fichier "fname"
			File file = new File(fname);
			// Si mode lecture :
			if (mode == OpenMode.R) {
				System.out.println("ouverture en lecture du fichier : " + fname);
				// Ouvrir le fichier en lecture
				file.setReadable(true);
				fr = new FileReader(file);
				// Lire le contenu du fichier
				br = new BufferedReader(fr);
				char[] buf = null;
				br.read(buf);
				String contentFile = new String(buf);
				br.close();
				// Découper le contenu du fichier en tableau de lignes
				lines = contentFile.split("\n");
				// Mettre OpenR à vrai pour signaler l'ouverture du descripteur de lecture
				OpenR = true;
			}
			// Si mode écriture :
			if (mode == OpenMode.W) {
				System.out.println("ouverture en écriture du fichier : " + fname);
				// Ouvrir le fichier en écriture
				file.setWritable(true);
				fw = new FileWriter(file);
				// Mettre OpenW à vrai pour signaler l'ouverture du descripteur en écriture
				OpenW = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		//mCMD.send(Commande.CMD_CLOSE,port);
		// fermer sock normalement je pense ou descripteurs
		try {
			// Si le fichier a été ouvert en lecture, fermer le descripteur de lecture
			if (OpenR) {
				fr.close();
			}
			// Si le fichier a été ouvert en écriture, fermer le descripteur d'écriture
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
		KV kv = null;
		// Si l'index est inférieur au nombre d'éléments du tableau de lignes
		if (index <= (lines.length)) {
			// Créer un KV ayant pour clé le numéro d'une ligne (index) et pour valeur le contenu de cette ligne (lines[index-1])
			kv = new KV(Integer.toString((int) index), lines[(int) index - 1]);
		}
		// Incrémenter l'index
		index++;
		return kv;
	}

	@Override
	public void write(KV record) {
		try {
			// Ecrire la ligne du KV en paramètre
			fw.write(record.v);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	// Obtenir l'index courant
	public long getIndex() {
		return this.index;
	}

	@Override
	// Obtenir le nom du fichier traiter
	public String getFname() {
		return this.fname;

	}

	@Override
	// Modifier le nom du fichier traiter
	public void setFname(String newFname) {
		File file = new File(fname);
		this.fname = newFname;
		file.renameTo(new File(file.getPath()+File.separator+newFname));
		
	}
	
}
