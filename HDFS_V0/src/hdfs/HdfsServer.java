package hdfs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import formats.Format;
import formats.Format.Commande;
import formats.Format.Type;
import formats.KV;
import util.Message;
import java.util.ArrayList;
import java.util.Iterator;

import static formats.Format.Commande.CMD_DELETE;


public class HdfsServer {

	private static String fname;
	private static File file;

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
					System.out.print(" Demande de lecture reçue ...");
					String fname = (String) m.receive();
					
					FileReader fr = new FileReader(fname);
					BufferedReader br = new BufferedReader(fr);
					
					//Creation de la chaine de caractères qui sera envoyée
					
					String strToSend = new String();
					
					String line = br.readLine() ;
					while (line != null ) {
						strToSend += line + "\n";
						line = br.readLine() ;
					}
					m.send(strToSend);
					System.out.println("fragment du fichier envoyé");
					
					break;
					
				case CMD_WRITE:
					System.out.print(" Demande d'écriture reçue ...");
					//On recoit une commande d'écriture avec le nom du fichier
					// et le type du fichier
					fname = (String) m.receive();
					Type fmt = (Type) m.receive();
					
					//Creation du fichier en local (dans le serveur)
					File file = new File(fname);
					FileWriter fw = new FileWriter(fname);
					BufferedWriter bw = new BufferedWriter(fw);

					// Reception de la chaine de caractères correspondant au fragment
					String strReceived = (String) m.receive();
					bw.write(strReceived, 0, strReceived.length()-1);
					//Fermeture du fichier
					bw.close();
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
		//m.close(ss); ?
		}

	}
}
