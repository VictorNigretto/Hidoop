package hdfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;

import formats.Format.Commande;
import formats.Format.Type;
import util.Message;


public class HdfsServer {


	public static void main(String[] args) throws IOException {

		int port = Integer.parseInt(args[0]);

		Message m = new Message();

		ServerSocket ss;
		ss = new ServerSocket(port);
		System.out.println("Serveur démarré :)");
		while (true) {
			// Récupérer la commande demandé
			m.openServer(ss);
			Commande cmd = (Commande) m.receive();
			// Traiter la commande reçu
			switch (cmd) {

				case CMD_READ:
					//On recoit une commande de lecture
					System.out.println(" Demande de lecture reçue ... ");
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
					// Envoie de la chaine de caractère 
					m.send(strToSend);
					System.out.println("fragment du fichier envoyé");
					} catch (FileNotFoundException fnfe) {
						System.out.println("fichier lu non existant");
					}
					break;
					
				case CMD_WRITE:
					System.out.print(" Demande d'écriture reçue ...");
					
					//On recoit une commande d'écriture avec le nom du fichier
					// et le type du fichier
					fname = (String) m.receive();
					Type fmt = (Type) m.receive();
					
					//Creation du fichier en local (dans le serveur)
					FileWriter fw = new FileWriter(fname);

					// Reception de la chaine de caractères correspondant au fragment
					String strReceived = (String) m.receive();
					fw.write(strReceived, 0, strReceived.length()-1);
					
					//Fermeture du fichier
					fw.close();
					System.out.println("fragment du fichier enregistré");
					break;
					
				case CMD_DELETE:
					// Supprimer contenu fragFile du serveur ; gérer en lste(remove file)
					System.out.print("Demande de suppression reçue ...");
					String Fname = (String) m.receive();
					File f = new File(Fname);
					f.delete();
					System.out.println("fichier supprimé");

				default:
					break;

			}
		}

	}
}
