/* une PROPOSITION de squelette, incomplète et adaptable... */

package hdfs;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import util.Message;

import formats.Format;
import formats.Format.Commande;
import formats.Format.Type;
import formats.KV;


public class HdfsClient {
	
	private static int servers[] = {6666,5555,4444};

    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    public static void HdfsDelete(String hdfsFname) {
		int nbServer = servers.length;
		System.out.println("Demande de suppression du fichier : " + hdfsFname + "..." );
		Message m = new Message();

		for (int i = 0; i < nbServer; i++) {
			//On supprime le fichier sur tous les serveurs
			m.openClient(servers[i]);
			m.send(Commande.CMD_DELETE);
			m.send(hdfsFname  + String.valueOf(i));
			m.close();
			System.out.println("envoyee au serveur " + i );
		}
	}
	
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, int repFactor) {
    	try {
    		Message m = new Message();
    		
			File fichier = new File(localFSSourceFname);

			int nbServer = servers.length;

			if (fmt == Format.Type.LINE || fmt == Format.Type.KV) {
				//On a donc un format line ou un format KV, dans les deux cas le fichier est écrits sous forme de fichier text 
				
				//Creation des buffers associés au fichier lue
				System.out.println("Demande d'écriture d'un fichier ...");
				FileReader fr = new FileReader(fichier);
				BufferedReader br = new BufferedReader(fr);
				
				//mise en place d'une marque au début du fichier (8192 est la taille max du BufferedReader)
				br.mark(8192);
				
				//Lire le fichier une première fois pour pouvoir compter le nombre de lignes 
				int nbLine = 0;
				while (br.readLine() != null) {
					nbLine++;
				}

				//Retour à la marque (placée au début du fichier
				br.reset();

				//Aides pour calculer le nombre de lignes envoyées à chaque serveurs
				int quotient = nbLine/nbServer;
				int reste = nbLine%nbServer;

				
				//Envoie de ces lignes à chaque serveurs
				for (int i=0 ; i<nbServer ; i++) {
					//Calcul du nombre de lignes envoyées au serveur
					int nbLineSent = quotient;
					if (reste != 0) {
						nbLineSent++;
						reste--;
					}
					//Envoie des lignes au serveur
					String str = new String();
					for (int j = 0 ; j<nbLineSent ; j++) {
						str += br.readLine() + "\n";
					}
					m.openClient(servers[i]);
					m.send(Commande.CMD_WRITE);
					System.out.println("envoyée au serveur " + i);
					m.send(fichier.getName() + String.valueOf(i));
					m.send(fmt);
					m.send(str);
					m.close();
					
					System.out.println("fragment envoyé au serveur " + i);

				}
				br.close();
				fr.close();
				} else {
					// On en reconnait pas le format
					System.out.println("Le format indiqué n'est pas reconnu par hdfs");
				}
    	} catch (FileNotFoundException fnfe) {
    		System.out.println("fichier local non existant");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    }

    public static void HdfsRead(String hdfsFname, String localFSDestFname) {
		System.out.println("Demande de lecture d'un fichier ...");
		Message m = new Message();
		File file = new File(localFSDestFname);
		try {
			// On récupère pour chaque serveurs les fragments de fichier et on écrit à la suite,
			// les lignes (ou les kv) dans un fichier local
			for (int i = 0; i < servers.length; i++) {
				
				//On envoie la commande au serveur et celui-ci renvoie le type et 
				//la chaine de caractères correspondant à son fragment.
				m.openClient(servers[i]);
				m.send(Commande.CMD_READ);
				System.out.println("envoyée au serveur " + i);
				m.send(hdfsFname + String.valueOf(i));
				String strReceived = (String) m.receive();
				m.close();
								
				//on rajoute donc les lignes reçu dans le fichier local à la fin
				FileWriter fw = new FileWriter(file, true);
				fw.write(strReceived, 0, strReceived.length());
			
				fw.close();
			}
			System.out.print("Ecriture des données dans un fichier local ...");
			System.out.println("données écrites");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}


	public static void main(String[] args) {
        // java HdfsClient <read|write> <line|kv> <file>

        try {
            if (args.length<2) {usage(); return;}

            switch (args[0]) {
              case "read": HdfsRead(args[1],args[2]); break;
              case "delete": HdfsDelete(args[1]); break;
              case "write": 
                Format.Type fmt;
                if (args.length<3) {usage(); return;}
                if (args[1].equals("line")) fmt = Format.Type.LINE;
                else if(args[1].equals("kv")) fmt = Format.Type.KV;
                else {usage(); return;}
                HdfsWrite(fmt,args[2],1);
            }	
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
