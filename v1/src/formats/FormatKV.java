package formats;


import java.io.*;
import java.util.ArrayList;

/* Permet de gérer un fichier répartit écrit sous forme de KV */
public class FormatKV implements Format{

	/*****************************************
	ATTRIBUTS
	*****************************************/
	private static final long serialVersionUID = 7697812117877244092L;
	
	// Gestion des fichiers
	private FileReader fr;
	private BufferedReader br; // pour lire
	private FileWriter fw; // pour écrire
	
	// Model : la liste des lignes du fichier
	// une ligne est de la forme cle<->valeur
	public ArrayList<String> lines = new ArrayList<String>();
	
	// Pour savoir comment est ouvert notre fichier
	private boolean OpenR = false;
	private boolean OpenW = false;

	private long index = 1; // index de lecture
	private String fname;	// nom du fichier

	/*****************************************
	CONSTRUCTEUR
	*****************************************/
	
    public FormatKV(String fname) {
    	this.fname = fname;
    }

	/*****************************************
	METHODES
	*****************************************/

    /* Méthode à appeler avant de pouvoir lire/écrire dans un fichier */
    @Override
	public void open(formats.Format.OpenMode mode)  {
		// Récupérer le fichier
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
				
				// Lire le contenu du fichier ligne par ligne et les mettre dans le tableau line en mémoire vive
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
    @Override
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
				// TODO Auto-generated catch block
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
			// On récupère la clé et la valeur du KV
			String[] line = lines.get((int) index - 1).split(KV.SEPARATOR);	
			kv = new KV(line[0], line[1]);
		}
		
		// Incrémenter l'index
		index++;
		
		return kv;
	}

    @Override
    public void write(KV record) {
        try {
        	// On écrit notre kv dans le fichier sous la forme cle<->valeur
			fw.write(record.k + KV.SEPARATOR + record.v + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	/*****************************************
	GETS && SETS
	*****************************************/

    @Override
    public long getIndex() {
        return this.index;
    }
    @Override
    public String getFname() {
        return this.fname;
    }
    @Override
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




