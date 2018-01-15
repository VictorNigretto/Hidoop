package application;

import java.util.StringTokenizer;

import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;
import formats.KV;
import map.MapReduce;
import ordo.Job;

public class MyMonteCarlo implements MapReduce{

    private static class HaltonSequence {
        /** Bases */
        static final int[] P = {2, 3};
        /** Maximum number of digits allowed */
        static final int[] K = {63, 40};

        private long index;
        private double[] x;
        private double[][] q;
        private int[][] d;

        /** Initialize to H(startindex),
         * so the sequence begins with H(startindex+1).
         */
        HaltonSequence(long startindex) {
            index = startindex;
            x = new double[K.length];
            q = new double[K.length][];
            d = new int[K.length][];
            for(int i = 0; i < K.length; i++) {
                q[i] = new double[K[i]];
                d[i] = new int[K[i]];
            }

            for(int i = 0; i < K.length; i++) {
                long k = index;
                x[i] = 0;

                for(int j = 0; j < K[i]; j++) {
                    q[i][j] = (j == 0? 1.0: q[i][j-1])/P[i];
                    d[i][j] = (int)(k % P[i]);
                    k = (k - d[i][j])/P[i];
                    x[i] += d[i][j] * q[i][j];
                }
            }
        }

        /** Compute next point.
         * Assume the current point is H(index).
         * Compute H(index+1).
         *
         * @return a 2-dimensional point with coordinates in [0,1)^2
         */
        double[] nextPoint() {
            index++;
            for(int i = 0; i < K.length; i++) {
                for(int j = 0; j < K[i]; j++) {
                    d[i][j]++;
                    x[i] += q[i][j];
                    if (d[i][j] < P[i]) {
                        break;
                    }
                    d[i][j] = 0;
                    x[i] -= (j == 0? 1.0: q[i][j-1]);
                }
            }
            return x;
        }
    }

	private static final long serialVersionUID = 1L;

	@Override
	/* Le but de cette fonction est de générer n points 
	 * dans un carré puis de vérifier si ils sont dans le 
	 * quart du cercle. */
	public void map(FormatReader reader, FormatWriter writer) {
		long nbInternes = 0;
		long nbExternes = 0;
		long debutSuite;
		long nbPoints;
		HaltonSequence hs;
		double[] point;
		double x, y;
		KV kv;

		//Pour chaque ligne du fichier
		while((kv = reader.read()) != null){
			//Récupérer l'indice de début de la suite et le nombre depoints à générer
			StringTokenizer st = new StringTokenizer(kv.v);
			debutSuite = Long.parseLong(st.nextToken());
			nbPoints = Long.parseLong(st.nextToken());
			
			//Générer les points à l'aide de la suite de Halton
            hs = new HaltonSequence(debutSuite);
		    for(long n = 0; n < nbPoints; n++){
		        point = hs.nextPoint();
		        x = point[0] - 0.5;
                y = point[1] - 0.5;
                if(x*x + y*y > 0.25) {
                    nbExternes ++;
                } else {
                    nbInternes ++;
                }
		    }
		}
		
		//Ecrire les resultats dans le fichier
		System.out.println("IN " + nbInternes + " OUT " + nbExternes);
		writer.write(new KV("In", String.valueOf(nbInternes)));
		writer.write(new KV("Out", String.valueOf(nbExternes)));
	}

	@Override
	public void reduce(FormatReader reader, FormatWriter writer) {
		float nbExternes = 0f;
		float nbInternes = 0f;
		float pi;
        KV kv;
		while ((kv = reader.read()) != null) {
			if((kv.k).equals("In")){
				nbInternes += Float.parseFloat(kv.v);
			} else if (kv.k.equals("Out")) {
				nbExternes += Float.parseFloat(kv.v);
			} else {
				System.out.println("On a pas lu la bonne clé du KV !!!");
			}
		}
		
		//Calculer la décimale de pi
		System.out.println("InFinal " + nbInternes + " OutFinal " + nbExternes);
		pi = 4f * (nbInternes / (nbInternes + nbExternes));
		System.out.println("Voici la valeur de PI = " + pi + " ! ");
		writer.write(new KV("Pi", String.valueOf(pi)));
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
