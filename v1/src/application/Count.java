package application;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

//import config.Project; // TODO ==> groupe Alexys/Marie
import formats.KV;

// Fait la même chose que MyMapReduce mais sur un fichier local
public class Count {

	// Il faut lancer le main avec un argument
	// Je ne sais pas à quoi il sert ...
	public static void main(String[] args) {

		try {
			// c'est juste pour faire des test sur le temps
            long t1 = System.currentTimeMillis();

            // On ouvre un fichier qui se trouve dans Project.PAHT + "data" + args[0] 
            // On lit tout le fichier
			Map<String,Integer> hm = new HashMap<>();
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(/*Project.PATH+*/"data/"+args[0])));
			
			// Tant que il y a des lignes à lire dans le fichier
			while (true) {
				// On récupère une ligne
				String l = lnr.readLine();
				if (l == null) break; // si elle était vide on sort de la boucle
				
				// On crée un StringTokenizer qui permet de découper des chaînes selon un certain motif
				StringTokenizer st = new StringTokenizer(l);
				// tant qu'on a des tokens
				while (st.hasMoreTokens()) {
					// On le compte
					String tok = st.nextToken();
					if (hm.containsKey(tok)) 
						hm.put(tok, hm.get(tok).intValue()+1);
					else 
						hm.put(tok, 1);
				}
			}
			// On écrit le fichier de résultat de notre comptage
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("count-res")));
			for (String k : hm.keySet()) {
				writer.write(k+"<->"+hm.get(k).toString());
				writer.newLine();
			}
			
			// On ferme proprement les fichiers
			writer.close();
			lnr.close();
			
			// On affiche le temps qu'a pris le calcul !
            long t2 = System.currentTimeMillis();
            System.out.println("time in ms ="+(t2-t1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
