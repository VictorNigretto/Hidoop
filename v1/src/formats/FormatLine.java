package formats;


import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;

import util.Message;
import formats.Format.OpenMode;
import formats.Format.Type;

/* Permet de gérer un fichier répartit écrit sous forme de KV */
public class FormatLine implements Format {

	/*****************************************
	ATTRIBUTS
	*****************************************/
	private static final long serialVersionUID = 1742753782732214897L;
	
	// Gestion des fichiers
	private FileReader fr;
	private BufferedReader br; // pour lire
	private FileWriter fw; // pour écrire
	
	// Pour savoir comment est ouvert notre fichier
	private boolean OpenR = false;
	private boolean OpenW = false;

	// Model : la liste des lignes du fichier
	public ArrayList<String> lines = new ArrayList<String>();

	private long index = 1; // index de lecture
	private String fname;	// nom du fichier
	
	/*****************************************
	CONSTRUCTEUR
	*****************************************/
	
	public FormatLine(String fname) {
		this.fname = fname;
	}
	
	/*****************************************
	METHODES
	*****************************************/
	
    /* Méthode à appeler avant de pouvoir lire/écrire dans un fichier */
	public void open(formats.Format.OpenMode mode)  {
		// Récupérer fichier "fname"
		File file = new File(fname);

		try {
			// Si mode lecture :
			if (mode == OpenMode.R) {
				// Ouvrir le fichier en lecture
				System.out.println("Ouverture en lecture du fichier : " + fname);
				file.setReadable(true);
				OpenR = true;
				fr = new FileReader(file);
				br = new BufferedReader(fr);	
				
				// Lire le contenu du fichier ligne par ligne et les mettre dans un tableau
				String line;
				while ((line = br.readLine()) != null) {
					lines.add(line);
				}
				br.close();
			}
			
			// Si mode écriture :
			if (mode == OpenMode.W) {
				// Ouvrir le fichier en écriture
				System.out.println("Ouverture en écriture du fichier : " + fname);
				file.setWritable(true);
				OpenW = true;
				
				// /!\ On considère que le fichier à écrire est vide !!!
				fw = new FileWriter(file,true);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Méthode à appeler après avoir fini de lire/écrire dans le fichier */
	public void close() {
		System.out.println("Fermeture du format");
		// Si le fichier a été ouvert en lecture, fermer le descripteur de lecture
		if (OpenR) {
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			OpenR = false;
		}
		// Si le fichier a été ouvert en écriture, fermer le descripteur d'écriture
		if (OpenW) {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
			OpenW = false;
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
	
	/*****************************************
	GETS && SETS
	*****************************************/

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
	public boolean isOpenR() {
		return OpenR;
	}
	public boolean isOpenW() {
		return OpenW;
	}
	public void setIndex(long index) {
		this.index = index;
	}
}
