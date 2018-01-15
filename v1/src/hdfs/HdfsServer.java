package hdfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import formats.Format.Commande;
import formats.Format.Type;
import util.Message;

/* L'application lancé sur un serveur.
 * Elle va permettre de remplir les demandes faites par le client
 * en écrivant, lisant et supprimant des fragments sur sa machine.
 */
public class HdfsServer {

	/*****************************************
	MAIN
	*****************************************/

	public static void main(String[] args) throws IOException {
		int port = Integer.parseInt(args[0]); // le port qu'utilise ce serveur est passé en paramètre
		Message m = new Message(); // pour écrire des messages
		ServerSocket ss = new ServerSocket(port); // pour attendre les messages
		
		System.out.println("Serveur démarré :)");
		// Le serveur attends qu'on le sollicite
		while (true) {
			// Récupérer la commande demandé
			m.openServer(ss);
			Commande cmd = (Commande) m.receive();
			
			// Traiter la commande reçu
			switch (cmd) {
				case CMD_READ:
					//On recoit une commande de lecture
					System.out.println(" Demande de lecture reçue ... ");
					
					// On attend de recevoir le nom du fichier à lire
					String fname = (String) m.receive();
					
					try {
						// Ouverture du fichier en lecture
						FileReader fr = new FileReader(fname);
						BufferedReader br = new BufferedReader(fr);
						
						//Creation de la chaine de caractères qui sera envoyée
						String strToSend = new String();
						String line = br.readLine() ;
						while (line != null ) {
							strToSend += line + "\n";
							line = br.readLine() ;
						}
						br.close();
						
						// Envoie du fragment demandé 
						m.send(strToSend);
						System.out.println("fragment du fichier envoyé");
					} catch (FileNotFoundException fnfe) {
						System.out.println("fichier lu non existant");
					}
					break;
					
				case CMD_WRITE:
					// On reçoit une commande d'écriture
					System.out.print(" Demande d'écriture reçue ...");
					
					// On recoit une commande d'écriture avec le nom du fichier
					// et le type du fichier
					fname = (String) m.receive();
					Type fmt = (Type) m.receive();
					
					//Creation du fichier en local (dans le serveur)
					FileWriter fw = new FileWriter(fname);

					// Reception de la chaine de caractères correspondant au fragment
					String strReceived = (String) m.receive();
					
					// On l'écrit en dur
					fw.write(strReceived, 0, strReceived.length()-1);
					
					//Fermeture du fichier
					fw.close();
					System.out.println("fragment du fichier enregistré");
					break;
					
				case CMD_DELETE:
					// On reçoit une commande de suppression
					System.out.print("Demande de suppression reçue ...");
					
					// On attend le nom du fichier à supprimer
					String Fname = (String) m.receive();
					
					// On le supprime
					File f = new File(Fname);
					f.delete();
					System.out.println("fichier supprimé");

				default:
					break;
			}
		}
	}
}
