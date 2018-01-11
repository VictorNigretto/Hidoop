package application;

import java.util.StringTokenizer;

import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;
import formats.KV;
import map.MapReduce;
import ordo.Job;

public class MyMonteCarlo implements MapReduce{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void map(FormatReader reader, FormatWriter writer) {
		long nbInternes = 0;
		long nbExternes = 0;
		long debutSuite;
		long nbPoints;
		double[] xInit;
		double[][] q;
		int d[][];
		
		double[] point;
		final int[] P = {2,3};
		final int[] K = {63, 40}; 
		
		
		KV kv;
		//Pour chaque ligne du fichier
		while((kv = reader.read()) != null){
			//Récupérer l'indice de début de la suite et le nombre depoints à générer
			StringTokenizer st = new StringTokenizer(kv.v, " ");
			debutSuite = Integer.parseInt(st.nextToken());
			nbPoints = Integer.parseInt(st.nextToken()) - debutSuite;
			
			//Générer les points à l'aide de la suite de Halton
		    xInit = new double[K.length];
		    q = new double[K.length][];
		    d = new int[K.length][];
		    
		    for(int i = 0; i < K.length; i++) {
		        q[i] = new double[K[i]];
		        d[i] = new int[K[i]];
		    }

		    for(int i = 0; i < K.length; i++) {
		    	long k = debutSuite;
		        xInit[i] = 0;
		        
		        for(int j = 0; j < K[i]; j++) {
		          q[i][j] = (j == 0? 1.0: q[i][j-1])/P[i];
		          d[i][j] = (int)(k % P[i]);
		          k = (k - d[i][j])/P[i];
		          xInit[i] += d[i][j] * q[i][j];
		        }
		    }
		    nbExternes = 0L;
		    nbInternes = 0L;
		    
		    for(long n = 0; n < nbPoints; n++){
		    	//Calculer le point suivant
		    	point = new double[K.length];
		    	for(int i=0; i<K.length; i++){
		    		for(int j = 0; j<K[i]; j++){
		    			d[i][j]++;
		    			point[i] += q[i][j];
		    			if(d[i][j] < P[i]){
		    				break;
		    			}
		    			d[i][j] = 0;
		    			point[i] -= (j == 0? 1.0: q[i][j-1]);
		    		}
		    	}
		    	//Vérifier si u point est externe ou interne
		    	double x = point [0] - 0.5;
		    	double y = point[1] - 0.5;
		    	if( x*x + y*y > 0.25) {
		    		nbExternes++;
		    	} else {
		    		nbInternes++;
		    	}

		    }
		}
		//Ecrire les resultats dans le fichier
		writer.write(new KV("In",String.valueOf(nbInternes)));
		writer.write(new KV("Out",String.valueOf(nbInternes)));

	}

	@Override
	public void reduce(FormatReader reader, FormatWriter writer) {
		// TODO Auto-generated method stub
		
	}
	
	// Avec un paramètre : le nom du fichier !
	public static void main(String args[]) {
		Job j = new Job();
		
        j.setInputFormat(Format.Type.LINE);
        j.setInputFname("MonteCarlo");
        
        // Pour le temps
        long t1 = System.currentTimeMillis();

        System.out.println("On a lancé le Monte Carlo");
		j.startJob(new MyMonteCarlo()); // on devra exécuter le programme principal dans startJob
		
		// On affiche le temps qu'à pris le MapReduce
		long t2 = System.currentTimeMillis();
        System.out.println("time in ms ="+(t2-t1));
        System.exit(0);
	}

}
