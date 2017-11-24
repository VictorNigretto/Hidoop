package formats;


import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;

import util.Message;
import formats.Format.OpenMode;
import formats.Format.Type;

public class FormatLine implements Format {


	private static final long serialVersionUID = 1742753782732214897L;
	private FileReader fr;
	private BufferedReader br;
	private FileWriter fw;
	
	private boolean OpenR = false;
	private boolean OpenW = false;

	public ArrayList<String> lines = new ArrayList<String>();

	private long index = 1; // index de lecture
	private String fname;	// nom du fichier

	public FormatLine(String fname) {
		this.fname = fname;
	}
	
<<<<<<< HEAD
	public void open(formats.Format.OpenMode mode)  {
=======
	public void open(OpenMode mode)  {
>>>>>>> 490f03727897fc9a51a9c5ee160e9188f248194d
		try {
			// Récupérer fichier "fname"
			File file = new File(fname);
			// Si mode lecture :
			if (mode == OpenMode.R) {
				System.out.println("Ouverture en lecture du fichier : " + fname);
				// Ouvrir le fichier en lecture
				file.setReadable(true);
				fr = new FileReader(file);
				// Lire le contenu du fichier ligne par ligne et les mettre dans un tableau
				br = new BufferedReader(fr);	
				String line;
				while ((line = br.readLine()) != null) {
					lines.add(line);
				}
				br.close();
				// Mettre OpenR à vrai pour signaler l'ouverture du descripteur de lecture
				OpenR = true;
			}
			// Si mode écriture :
			if (mode == OpenMode.W) {
				System.out.println("Ouverture en écriture du fichier : " + fname);
				// Ouvrir le fichier en écriture
				file.setWritable(true);
				fw = new FileWriter(file,true);	
				// Mettre OpenW à vrai pour signaler l'ouverture du descripteur en écriture
				OpenW = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		System.out.println("Fermeture du format");
		try {
			// Si le fichier a été ouvert en lecture, fermer le descripteur de lecture
			if (OpenR) {
				fr.close();
				OpenR = false;
			}
			// Si le fichier a été ouvert en écriture, fermer le descripteur d'écriture
			if (OpenW) {
				fw.close();	
				OpenW = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public KV read() {
		KV kv = null;
		// Si l'index est inférieur au nombre d'éléments du tableau de lignes
		if (index <= (lines.size())) {
			// Créer un KV ayant pour clé le numéro d'une ligne (index) et pour valeur le contenu de cette ligne (lines[index-1])
			kv = new KV(Integer.toString((int) index), lines.get((int) index - 1));
		}
		// Incrémenter l'index
		index++;
		return kv;
	}

	@Override
	public void write(KV record) {
		try {		
			// Ecrire la ligne du KV en paramètre
			fw.write(record.v + "\n");
		} catch (IOException e) {
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
	// Ne modifie que l'attribut de FormatLine
	public void setFname(String newFname) {
		this.fname = newFname;	
	}
	
}
