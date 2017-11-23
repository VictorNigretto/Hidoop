package formats;


import util.Message;


import java.io.*;
import java.util.ArrayList;

import formats.Format.OpenMode;

public class FormatKV implements Format{


// Essayer de factoriser en classe générique classes envoi et reception

	private FileReader fr;
	private BufferedReader br;
	private FileWriter fw;
	
	private boolean OpenR = false;
	private boolean OpenW = false;

	public ArrayList<String> lines = new ArrayList<String>();

	private long index = 1; // index de lecture
	private String fname;	// nom du fichier

        public FormatKV(String fname) {
            this.fname = fname;
        }

    	public void open(formats.Format.OpenMode mode)  {
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
    			// TODO Auto-generated catch block
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
    			// TODO Auto-generated catch block
    			e.printStackTrace();
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
				fw.write(record.k + KV.SEPARATOR + record.v + "\n");
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
        public void setFname(String newFname) {
        	this.fname = newFname;
        }

    }



