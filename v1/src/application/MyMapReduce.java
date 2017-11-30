package application;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import map.MapReduce;
import ordo.Job;
import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;
import formats.KV;

// Compte le nombre d'occurences de chaques mots d'un fichier stocké en HDFS
public class MyMapReduce implements MapReduce {
	// Je ne sais pas à quoi ça sert
	private static final long serialVersionUID = 1L;

	// MapReduce program that computes word counts
	public void map(FormatReader reader, FormatWriter writer) {
		
		Map<String,Integer> hm = new HashMap<>();
		KV kv;
		while ((kv = reader.read()) != null) {
			StringTokenizer st = new StringTokenizer(kv.v);
			while (st.hasMoreTokens()) {
				String tok = st.nextToken();
				if (hm.containsKey(tok))
					hm.put(tok, hm.get(tok).intValue()+1);
				else
					hm.put(tok, 1);
			}
		}
		for (String k : hm.keySet())
			writer.write(new KV(k,hm.get(k).toString()));
	}
	
	public void reduce(FormatReader reader, FormatWriter writer) {
		
        Map<String,Integer> hm = new HashMap<>();
		KV kv;
		while ((kv = reader.read()) != null) {
			if (hm.containsKey(kv.k))
				hm.put(kv.k, hm.get(kv.k)+Integer.parseInt(kv.v));
			else
				hm.put(kv.k, Integer.parseInt(kv.v));
		}
		for (String k : hm.keySet())
			writer.write(new KV(k,hm.get(k).toString()));
	}
	
	// Avec un paramètre : le nom du fichier !
	public static void main(String args[]) {
		// On crée un Job
		// Qu'est-ce qu'un Job ?
		// C'est l'application qui va s'occuper de lancer tous les maps
		Job j = new Job();
		// On lui dit le format et le nom du fichier qui nous intéressent
        j.setInputFormat(Format.Type.LINE);
        j.setInputFname(args[0]);
        
        // Pour le temps
        long t1 = System.currentTimeMillis();

        System.out.println("On a lancé le MyMapReduce !");
		j.startJob(new MyMapReduce()); // on devra exécuter le programme principal dans startJob
		
		// On affiche le temps qu'à pris le MapReduce
		long t2 = System.currentTimeMillis();
        System.out.println("time in ms ="+(t2-t1));
        System.exit(0);
	}
}
