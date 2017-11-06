package formats;

import java.io.*;
import java.util.HashMap;

public class FormatLineLocal implements Format {

    private String fileName;
    private HashMap<Integer, String> lignes; // l'ensemble des lignes du fichier stocké sous la forme <num_ligne, chaîne_ligne>
    private int index; // l'indice de la prochaine ligne qui sera lu par le read

    // On a deux types de constructeurs
    // Le constructeur vide qui crée un ensemble de lignes vide
    public FormatLineLocal() {
        this.fileName = null;
        this.lignes = new HashMap<>();
        this.index = 0;
    }

    // Et le constructeur avec le chemin d'un fichier en paramètre
    // qui initialise l'ensemble de ligne avec les lignes du fichier
    public FormatLineLocal(String pathFile) {
        this();
        this.fileName = pathFile;
        // ouvrir le fichier
        File f = new File(pathFile);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));

            // lire le fichier et l'écrire dans l'ensemble de lignes
            String line;
            Integer numLine = new Integer(0);
            while((line = br.readLine()) != null) {
                this.lignes.put(numLine, line);
                numLine ++;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    // Pré : Il faut absolument qu'il n'y ait pas de "trous" dans la hashmap
    public KV read() {
        // Si on ne peut pas lire la valeur de la key
        if(lignes.get(index) == null) {
            // C'est qu'on est à la fin du format, donc on retourne au début
            index = 0;
            return null;

        // Si on peut lire la valeur
        } else {
            // alors renvoie le KV correspondant
            KV kv = new KV(new Integer(index).toString(), lignes.get(index));
            index ++; // en passant à la ligne suivante
            return kv;
        }
   }

    @Override
    public void write(KV record) {
        lignes.put(Integer.parseInt(record.k), record.v);
    }

    @Override
    public void open(OpenMode mode) {
        // On ne sait pas quoi mettre ayant de l'importance ici
    }

    @Override
    public void close() {
        // On ne sait pas quoi mettre ayant de l'importance ici
    }

    @Override
    public long getIndex() {
        return index;
    }

    @Override
    public String getFname() {
        return fileName;
    }

    @Override
    public void setFname(String fname) {
        fileName = fname;
    }
}


