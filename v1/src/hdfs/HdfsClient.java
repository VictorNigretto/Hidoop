/* une PROPOSITION de squelette, incomplète et adaptable... */

package hdfs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import formats.Format;
import formats.Format.Commande;
import util.Message;

/* L'application lancée sur la machine du client.
 * Elle va lui permettre de lire, écrire et détruire des fichiers
 * sur le serveur HDFS.
 */
public class HdfsClient {

	/*****************************************
	METHODES
	*****************************************/

	/* Pour indiquer à l'utilisateur comment utiliser son outil. */
    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    /* Pour supprimer un fichier du serveur HDFS */
    public static void HdfsDelete(String hdfsFname) {
    	//Recuperer la liste des machines auxquelles on va envoyer la demande de suppression
    	//On interroge le NameNode
    	NameNode nn = null;
    	ArrayList<Machine> machinesSupprimer = null;

		try {
			nn = (NameNode) Naming.lookup("//localhost:1199/NameNode");
			machinesSupprimer = (ArrayList<Machine>) nn.getMachinesFichier(hdfsFname);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		// Si le fichier existe
		try {
			if(nn.fileExists(hdfsFname)) {
                Message m = new Message(); // Pour envoyer des messages
                System.out.println("Demande de suppression du fichier : " + hdfsFname + "...");

                // Pour chaque serveur, on va supprimer les fragments qu'il contient
                for (Machine mac : machinesSupprimer) {
                    ArrayList<String> fragments = null;

                    try {
                        fragments = (ArrayList<String>) nn.getAllFragmentFichierMachine(mac, hdfsFname);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    for (String frag : fragments) {
                        m.openClient(mac.getNom(), mac.getPort());
                        m.send(Commande.CMD_DELETE);
                        m.send(frag);
                        m.close();
                    }
                }

            // Si le fichier n'existe pas
            } else {
                System.out.println("Le fichier " + hdfsFname + " n'existe pas dans la base de données HDFS !");
            }
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		// On indique au NameNode qu'on a supprimé le fichier
		try {
			nn.supprimeFichierHdfs(hdfsFname);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
    /* Pour écrire un fichier local sur le serveur HDFS
     * On doit pour cela indiquer le format avec lequel on voudra l'écrire.
     * Le nom du fichier.
     * Le nombre de fois que l'on veut que ce fichier soit dupliqué sur le serveur.
     */
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, int repFactor) {
    	
    	//TODO : envoyer le nombre de serveurs au client
		Message m = new Message(); // Pour pouvoir envoyer des messages
		File fichier = new File(localFSSourceFname); // le fichier que l'on lit

        //Recupération des machines disponibles en écriture
        NameNode nn = null;
        try {
            nn = (NameNode) Naming.lookup("//localhost:1199/NameNode");
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        List<Machine> machines = null;
		try {
			machines = nn.getMachines();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        int nbServeurs = machines.size();
        int factReplication = 0;
		try {
			factReplication = nn.getFacteurdereplication();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	try {
			//On a donc un format line ou un format KV, dans les deux cas le fichier est écrits sous forme de fichier text 
			if (fmt == Format.Type.LINE || fmt == Format.Type.KV) {
				
				//Creation des buffers associés au fichier lue
				System.out.println("Demande d'écriture d'un fichier ...");
				FileReader fr = new FileReader(fichier);
				BufferedReader br = new BufferedReader(fr);

                /*Debut du nouveau code*/
				//Lire la taille du fichier
                long tailleFichier = fichier.length();
                int numFragment = 0;

                //Selectionner la premiere machine qui recoit un fragment au hasard.                
                Machine firstMachine = nn.getMachineMoinsPleine();
                
                //Dire au NameNode qu'on va ajouter un fichier à la base de données
                nn.ajoutFichierHdfs(localFSSourceFname);
                
                // On calcul le nombre de fragments que l'on va envoyer !
                int nbFragments = 0;
                br.mark(999_999_999);
                while(br.readLine() != null && nbFragments < nbServeurs) {
                	nbFragments ++;
				}
                br.reset();

				//On envoie des fragments de fichiers aux serveurs
                for(int k = 0; k < nbFragments; k++) {

                    long tailleFrag = 0L;
                    String str = new String();
                	String ligne = null;

                    //On calcule le String à envoyer pour un fragment
                    while ((tailleFichier / nbFragments) > tailleFrag && (ligne = br.readLine()) != null) {
                    	ligne += "\n";
                        str += ligne;
                        tailleFrag += ligne.getBytes().length;
                    }

                    //On l'envoie
                    for (int i = 0; i < factReplication; i++) {
                        m.openClient(firstMachine.getNom(),firstMachine.getPort());
                        m.send(Commande.CMD_WRITE);
                        System.out.println("envoyée au serveur " + firstMachine.getNom());

                        String nomFragment = fichier.getName() + String.valueOf(numFragment);
                        // On envoie le nom du fichier concaténé avec son numéro de fragment
                        m.send(nomFragment);
                        // On envoie le format du fichier
                        m.send(fmt);

                        // On envoie le contenu du fichier puis on ferme les sockets
                        m.send(str);
                        m.close();
                        
                        //Dire au NameNode qu'on a mis le fragment dans firstMachine
                        nn.ajoutFragmentMachine(firstMachine, localFSSourceFname, nomFragment, numFragment);
                        
                        //Choisir la machine suivante
                        int indice = machines.indexOf(firstMachine);
                        if(indice+1 == machines.size()){
                            firstMachine = machines.get(0);
                        } else {
                            firstMachine = machines.get(indice + 1);
                        }           
                    }
                    numFragment++;
                }

                /*Fin du nouveau code*/
/**
				//Aides pour calculer le nombre de lignes envoyées à chaque serveurs
				int quotient = nbLine/nbServeurs;
				int reste = nbLine%nbServer;
				
				//Envoie de ces lignes à chaque serveurs
				for (int i=0 ; i < nbServer ; i++) {
					//Calcul du nombre de lignes envoyées au serveur
					int nbLineSent = quotient;
					if (reste != 0) {
						nbLineSent++;
						reste--;
					}
					//Envoie des lignes au serveur
					String str = new String();
					for (int j = 0 ; j < nbLineSent ; j++) {
						str += br.readLine() + "\n";
					}
					m.openClient(ordis[i],servers[i]);
					m.send(Commande.CMD_WRITE);
					System.out.println("envoyée au serveur " + i);
					
					// On envoie le nom du fichier concaténé avec son numéro de serveur
					m.send(fichier.getName() + String.valueOf(i));
					
					// On envoie le format du fichier
					m.send(fmt);
					
					// On envoie le contenu du fichier puis on ferme les sockets
					m.send(str);
					m.close();
					
					System.out.println("fragment envoyé au serveur " + i);
				}
 */
				
				// On ferme le fichier
				br.close();
				fr.close();
				
			} else {
				// On en reconnait pas le format
				System.out.println("Le format indiqué n'est pas reconnu par hdfs");
			}
    	} catch (FileNotFoundException fnfe) {
			System.out.println("Le fichier " + fichier.getAbsolutePath() + " n'a pas pu être trouvé");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    /* Pour lire un fichier du serveur HDFS, celui-ci sera écrit en local sous le nom spécifié */
    public static void HdfsRead(String hdfsFname, String localFSDestFname) {
		System.out.println("Demande de lecture d'un fichier ...");
		Message m = new Message(); // Pour envoyer des messages
		File file = new File(localFSDestFname); // le fichier local dans lequel écrire
		NameNode nn = null;
		
		try {
			nn = (NameNode) Naming.lookup("//localhost:1199/NameNode");

			//Lire seulement si le fichier existe
			if (nn.fileExists(hdfsFname)) {
                FileWriter fw = new FileWriter(file, true);
                //On récupère la liste des fragments fichier, dans l'ordre
                ArrayList<String> fragments = (ArrayList<String>) nn.getFragments(hdfsFname);


				//TODO : gerer le fait qu'une machine peut etre K.O

				for (String frag : fragments) {
					//une machine qui contient frag
					Machine mac = nn.getMachineFragment(frag, null);
					m.openClient(mac.getNom(), mac.getPort());
					m.send(Commande.CMD_READ);
					m.send(frag);
					String strReceived = (String) m.receive();
					m.close();
					System.out.println("envoyée au serveur " + mac.getNom() + ":" + mac.getPort());
					fw.write(strReceived, 0, strReceived.length());
				}

				fw.close();
				System.out.print("Ecriture des données dans un fichier local ...");
				System.out.println("Données écrites.");
			} else {
				System.out.println("Le fichier " + hdfsFname + " n'existe pas dans la base de données HDFS");
			}
			// On ferme notre fichier
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	/*****************************************
	MAIN
	*****************************************/

	public static void main(String[] args) {
        // java HdfsClient <read|write> <line|kv> <file>

        double time = System.currentTimeMillis();
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
        System.out.println("Temps : " + (System.currentTimeMillis() - time));
    }

}
